package com.example.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FinanceRepository(private val dao: FinanceDao) {

    // --- Data Flows ---
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val allLoans: Flow<List<Loan>> = dao.getAllLoans()
    val allInvestments: Flow<List<Investment>> = dao.getAllInvestments()
    val allBudgets: Flow<List<Budget>> = dao.getAllBudgets()

    // --- Simulated Sync States ---
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _isOnline = MutableStateFlow(true) // Simulated internet state
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    // Flow that calculates how many items are currently offline/unsynced
    val unsyncedCount: Flow<Int> = combine(
        allTransactions, allLoans, allInvestments, allBudgets
    ) { txs, loans, invs, budgets ->
        txs.count { !it.isSynced } +
        loans.count { !it.isSynced } +
        invs.count { !it.isSynced } +
        budgets.count { !it.isSynced }
    }

    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    // --- Set Online Status & Auto-Sync ---
    fun setOnlineStatus(online: Boolean) {
        _isOnline.value = online
        if (online) {
            triggerAutoSync()
        }
    }

    // --- Add/Edit/Delete Operations ---
    suspend fun insertTransaction(transaction: Transaction) {
        // Save locally first (offline first)
        val localTx = transaction.copy(isSynced = false)
        dao.insertTransaction(localTx)
        if (_isOnline.value) {
            triggerAutoSync()
        }
    }

    suspend fun deleteTransaction(id: Long) {
        dao.deleteTransaction(id)
    }

    suspend fun insertLoan(loan: Loan) {
        val localLoan = loan.copy(isSynced = false)
        dao.insertLoan(localLoan)
        if (_isOnline.value) {
            triggerAutoSync()
        }
    }

    suspend fun deleteLoan(id: Long) {
        dao.deleteLoan(id)
    }

    suspend fun insertInvestment(investment: Investment) {
        val localInv = investment.copy(isSynced = false)
        dao.insertInvestment(localInv)
        if (_isOnline.value) {
            triggerAutoSync()
        }
    }

    suspend fun deleteInvestment(id: Long) {
        dao.deleteInvestment(id)
    }

    suspend fun insertBudget(budget: Budget) {
        val localBudget = budget.copy(isSynced = false)
        dao.insertBudget(localBudget)
        if (_isOnline.value) {
            triggerAutoSync()
        }
    }

    suspend fun deleteBudget(category: String) {
        dao.deleteBudget(category)
    }

    // --- Trigger Background Sync ---
    fun triggerAutoSync() {
        if (!_isOnline.value || _isSyncing.value) return
        
        repositoryScope.launch {
            _isSyncing.value = true
            try {
                // Simulate network latency for synchronization
                delay(1500)

                // 1. Sync Transactions
                val unsyncedTxs = dao.getUnsyncedTransactions()
                for (tx in unsyncedTxs) {
                    dao.updateTransaction(tx.copy(isSynced = true))
                }

                // 2. Sync Loans
                val unsyncedLoans = dao.getUnsyncedLoans()
                for (loan in unsyncedLoans) {
                    dao.updateLoan(loan.copy(isSynced = true))
                }

                // 3. Sync Investments
                val unsyncedInvs = dao.getUnsyncedInvestments()
                for (inv in unsyncedInvs) {
                    dao.updateInvestment(inv.copy(isSynced = true))
                }

                // 4. Sync Budgets
                val unsyncedBudgets = dao.getUnsyncedBudgets()
                for (budget in unsyncedBudgets) {
                    dao.updateBudget(budget.copy(isSynced = true))
                }

            } catch (e: Exception) {
                // Handle exceptions silently in this simulated sync
            } finally {
                _isSyncing.value = false
            }
        }
    }

    // Populate initial dummy data if the database is completely empty
    suspend fun prepopulateIfEmpty() {
        // We can check transactions or budgets. If completely empty, insert some default transactions and budgets
    }
}
