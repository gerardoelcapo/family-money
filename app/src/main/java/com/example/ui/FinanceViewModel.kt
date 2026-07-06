package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Budget
import com.example.data.FinanceRepository
import com.example.data.Investment
import com.example.data.Loan
import com.example.data.Transaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.Calendar

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    // --- State Streams from Repository ---
    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loans: StateFlow<List<Loan>> = repository.allLoans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val investments: StateFlow<List<Investment>> = repository.allInvestments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgets: StateFlow<List<Budget>> = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isSyncing: StateFlow<Boolean> = repository.isSyncing
    val isOnline: StateFlow<Boolean> = repository.isOnline
    val unsyncedCount: StateFlow<Int> = repository.unsyncedCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // --- Computed Financial States ---
    val summaryState: StateFlow<FinancialSummary> = combine(
        transactions, loans, investments, budgets
    ) { txList, loanList, invList, budgetList ->
        
        var totalIncome = 0.0
        var totalExpense = 0.0
        val categoryExpenses = mutableMapOf<String, Double>()

        // Calculate current month's dates
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)

        for (tx in txList) {
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.date }
            // Filter by current month to show monthly finances
            if (txCal.get(Calendar.MONTH) == currentMonth && txCal.get(Calendar.YEAR) == currentYear) {
                if (tx.type == "INCOME") {
                    totalIncome += tx.amount
                } else {
                    totalExpense += tx.amount
                    categoryExpenses[tx.category] = (categoryExpenses[tx.category] ?: 0.0) + tx.amount
                }
            }
        }

        val balance = totalIncome - totalExpense

        // Investments
        val totalInvested = invList.sumOf { it.amount }
        val currentInvestmentsValue = invList.sumOf { it.currentValue }
        val investmentGain = currentInvestmentsValue - totalInvested

        // Loans (Debts)
        val totalLent = loanList.filter { it.type == "LENT" }.sumOf { it.remainingAmount }
        val totalBorrowed = loanList.filter { it.type == "BORROWED" }.sumOf { it.remainingAmount }

        // Budget Alerts
        val budgetAlerts = mutableListOf<BudgetAlert>()
        for (budget in budgetList) {
            val spent = categoryExpenses[budget.category] ?: 0.0
            if (spent > budget.limitAmount) {
                budgetAlerts.add(
                    BudgetAlert(
                        category = budget.category,
                        limit = budget.limitAmount,
                        spent = spent,
                        percentage = (spent / budget.limitAmount) * 100
                    )
                )
            }
        }

        // Over-indebtedness indicator (Alerta de Sobreendeudamiento)
        // If total borrowed debt exceeds 40% of standard monthly income (using totalIncome or a fallback if 0)
        val debtToIncomeRatio = if (totalIncome > 0) totalBorrowed / totalIncome else 0.0
        val isOverIndebted = totalBorrowed > 0 && (debtToIncomeRatio > 0.4 || totalBorrowed > 5000.0 && totalIncome < 1500.0)

        FinancialSummary(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = balance,
            categoryExpenses = categoryExpenses,
            totalInvested = totalInvested,
            currentInvestmentsValue = currentInvestmentsValue,
            investmentGain = investmentGain,
            totalLent = totalLent,
            totalBorrowed = totalBorrowed,
            budgetAlerts = budgetAlerts,
            isOverIndebted = isOverIndebted,
            debtToIncomeRatio = debtToIncomeRatio
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialSummary())

    init {
        // Pre-populate database with friendly dummy data if completely empty
        viewModelScope.launch {
            checkAndPrepopulateData()
        }
    }

    // --- Actions ---
    fun setOnline(online: Boolean) {
        repository.setOnlineStatus(online)
    }

    fun triggerAutoSync() {
        repository.triggerAutoSync()
    }

    fun addTransaction(amount: Double, type: String, category: String, note: String, date: Long) {
        viewModelScope.launch {
            val tx = Transaction(
                amount = amount,
                type = type,
                category = category,
                note = note,
                date = date
            )
            repository.insertTransaction(tx)
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun addLoan(title: String, amount: Double, interestRate: Double, type: String, dueDate: Long) {
        viewModelScope.launch {
            val loan = Loan(
                title = title,
                amount = amount,
                remainingAmount = amount,
                interestRate = interestRate,
                type = type,
                dueDate = dueDate
            )
            repository.insertLoan(loan)
        }
    }

    fun payLoanAmount(loan: Loan, amountPaid: Double) {
        viewModelScope.launch {
            val updatedRemaining = (loan.remainingAmount - amountPaid).coerceAtLeast(0.0)
            val updatedLoan = loan.copy(remainingAmount = updatedRemaining)
            repository.insertLoan(updatedLoan)
            
            // Also register as an EXPENSE transaction under "Préstamos" category
            addTransaction(
                amount = amountPaid,
                type = "EXPENSE",
                category = "Préstamos",
                note = "Pago a ${loan.title}",
                date = System.currentTimeMillis()
            )
        }
    }

    fun deleteLoan(id: Long) {
        viewModelScope.launch {
            repository.deleteLoan(id)
        }
    }

    fun addInvestment(title: String, amount: Double, currentValue: Double, expectedReturn: Double, category: String) {
        viewModelScope.launch {
            val investment = Investment(
                title = title,
                amount = amount,
                currentValue = currentValue,
                expectedReturn = expectedReturn,
                category = category,
                date = System.currentTimeMillis()
            )
            repository.insertInvestment(investment)
        }
    }

    fun updateInvestmentValue(investment: Investment, newCurrentValue: Double) {
        viewModelScope.launch {
            val updated = investment.copy(currentValue = newCurrentValue)
            repository.insertInvestment(updated)
        }
    }

    fun deleteInvestment(id: Long) {
        viewModelScope.launch {
            repository.deleteInvestment(id)
        }
    }

    fun addBudget(category: String, limitAmount: Double) {
        viewModelScope.launch {
            val budget = Budget(
                category = category,
                limitAmount = limitAmount
            )
            repository.insertBudget(budget)
        }
    }

    fun deleteBudget(category: String) {
        viewModelScope.launch {
            repository.deleteBudget(category)
        }
    }

    private suspend fun checkAndPrepopulateData() {
        // Query current flows in a background way
        // Let's check if budgets are empty.
        // We'll let Room do insertions if they're empty.
        val currentBudgets = repository.allBudgets.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        val currentTxs = repository.allTransactions.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
        
        // Wait briefly for state initialization
        delay(300)
        
        if (currentBudgets.value.isEmpty()) {
            // Setup default budgets
            repository.insertBudget(Budget("Alimentos", 300.0))
            repository.insertBudget(Budget("Transporte", 100.0))
            repository.insertBudget(Budget("Servicios", 200.0))
            repository.insertBudget(Budget("Entretenimiento", 80.0))
        }

        if (currentTxs.value.isEmpty()) {
            val now = System.currentTimeMillis()
            // Incomes
            repository.insertTransaction(Transaction(amount = 2500.0, type = "INCOME", category = "Sueldo", note = "Sueldo Mensual", date = now))
            repository.insertTransaction(Transaction(amount = 120.0, type = "INCOME", category = "Ventas", note = "Venta de artículos usados", date = now - 86400000))
            
            // Expenses
            repository.insertTransaction(Transaction(amount = 320.0, type = "EXPENSE", category = "Alimentos", note = "Supermercado quincenal", date = now - 172800000))
            repository.insertTransaction(Transaction(amount = 45.0, type = "EXPENSE", category = "Transporte", note = "Gasolina", date = now - 259200000))
            repository.insertTransaction(Transaction(amount = 150.0, type = "EXPENSE", category = "Servicios", note = "Internet y Electricidad", date = now - 345600000))
            repository.insertTransaction(Transaction(amount = 95.0, type = "EXPENSE", category = "Entretenimiento", note = "Cena familiar", date = now - 432000000))

            // Prepopulate an investment
            repository.insertInvestment(Investment(title = "ETF S&P 500", amount = 1500.0, currentValue = 1680.0, expectedReturn = 10.5, category = "Acciones", date = now - 864000000))
            repository.insertInvestment(Investment(title = "Plazo Fijo Banco", amount = 1000.0, currentValue = 1040.0, expectedReturn = 4.0, category = "Plazo Fijo", date = now - 864000000))

            // Prepopulate loans
            repository.insertLoan(Loan(title = "Crédito Automotriz", amount = 12000.0, remainingAmount = 8500.0, interestRate = 8.5, dueDate = now + 864000000, type = "BORROWED"))
            repository.insertLoan(Loan(title = "Préstamo a Juan", amount = 400.0, remainingAmount = 250.0, interestRate = 0.0, dueDate = now + 1728000000, type = "LENT"))
        }
    }
}

// --- Helper Data Classes ---

data class FinancialSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val categoryExpenses: Map<String, Double> = emptyMap(),
    val totalInvested: Double = 0.0,
    val currentInvestmentsValue: Double = 0.0,
    val investmentGain: Double = 0.0,
    val totalLent: Double = 0.0,
    val totalBorrowed: Double = 0.0,
    val budgetAlerts: List<BudgetAlert> = emptyList(),
    val isOverIndebted: Boolean = false,
    val debtToIncomeRatio: Double = 0.0
)

data class BudgetAlert(
    val category: String,
    val limit: Double,
    val spent: Double,
    val percentage: Double
)

// Factory implementation
class FinanceViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
