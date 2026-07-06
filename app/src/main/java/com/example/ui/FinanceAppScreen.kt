package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.SensorsOff
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Budget
import com.example.data.Investment
import com.example.data.Loan
import com.example.data.Transaction
import com.example.ui.theme.AlertCoral
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.InvestmentGold
import com.example.ui.theme.OnSlateText
import com.example.ui.theme.OnSlateTextSecondary
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateSurface
import com.example.ui.theme.SlateSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Helper extension to format currency
fun Double.toCurrency(): String = String.format(Locale.US, "$%,.2f", this)

// Format date
fun Long.toDateString(): String {
    val formatter = SimpleDateFormat("dd MMM, yyyy", Locale("es", "ES"))
    return formatter.format(Date(this))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceAppScreen(viewModel: FinanceViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val transactions by viewModel.transactions.collectAsState()
    val loans by viewModel.loans.collectAsState()
    val investments by viewModel.investments.collectAsState()
    val budgets by viewModel.budgets.collectAsState()
    val summary by viewModel.summaryState.collectAsState()

    val isSyncing by viewModel.isSyncing.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()

    // Dialog state
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var showAddLoanDialog by remember { mutableStateOf(false) }
    var showAddInvestmentDialog by remember { mutableStateOf(false) }
    var showAddBudgetDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(SlateSurface)
                    .fillMaxWidth()
            ) {
                // Sync status bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isOnline) SlateSurfaceVariant else SlateSurface.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOnline) Icons.Filled.Sensors else Icons.Filled.SensorsOff,
                            contentDescription = "Sync Connection Status",
                            tint = if (isOnline) IncomeGreen else OnSlateTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isOnline) "Modo Online Autonube" else "Modo Offline (Guardado Local)",
                            fontSize = 11.sp,
                            color = if (isOnline) OnSlateText else OnSlateTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isSyncing) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = "Syncing with cloud",
                                tint = GreenPrimary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .testTag("sync_loading_indicator")
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sincronizando...",
                                fontSize = 11.sp,
                                color = GreenPrimary
                            )
                        } else {
                            Icon(
                                imageVector = if (unsyncedCount == 0) Icons.Filled.CloudDone else Icons.Filled.CloudQueue,
                                contentDescription = "Cloud Synced",
                                tint = if (unsyncedCount == 0) IncomeGreen else InvestmentGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (unsyncedCount == 0) "Nube Al Día" else "Pendiente: $unsyncedCount ítems",
                                fontSize = 11.sp,
                                color = if (unsyncedCount == 0) IncomeGreen else InvestmentGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Primary M3 Tab Selector
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = SlateSurface,
                    contentColor = GreenPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = GreenPrimary,
                            height = 3.dp
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("tab_dashboard"),
                        text = {
                            Text(
                                "Resumen",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.AccountBalance,
                                contentDescription = "Tab Resumen"
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("tab_transactions"),
                        text = {
                            Text(
                                "Transacciones",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.List,
                                contentDescription = "Tab Transacciones"
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        modifier = Modifier
                            .height(56.dp)
                            .testTag("tab_finance_products"),
                        text = {
                            Text(
                                "Crédito / Inv",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                contentDescription = "Tab Créditos e Inversiones"
                            )
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showAddTransactionDialog = true },
                    containerColor = GreenPrimary,
                    contentColor = SlateBackground,
                    modifier = Modifier.testTag("add_transaction_fab")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar Transacción"
                    )
                }
            } else if (selectedTab == 2) {
                FloatingActionButton(
                    onClick = {
                        // Display options or default to Loan adding
                        showAddLoanDialog = true
                    },
                    containerColor = GreenPrimary,
                    contentColor = SlateBackground,
                    modifier = Modifier.testTag("add_asset_fab")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Agregar Producto"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateBackground)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardView(
                    summary = summary,
                    isOnline = isOnline,
                    onOnlineToggle = { viewModel.setOnline(it) },
                    onManualSync = { viewModel.triggerAutoSync() },
                    budgets = budgets,
                    onAddBudgetClick = { showAddBudgetDialog = true },
                    onDeleteBudget = { viewModel.deleteBudget(it) }
                )
                1 -> TransactionsView(
                    transactions = transactions,
                    onDeleteTransaction = { viewModel.deleteTransaction(it) }
                )
                2 -> FinanceProductsView(
                    loans = loans,
                    investments = investments,
                    onAddLoanClick = { showAddLoanDialog = true },
                    onAddInvestmentClick = { showAddInvestmentDialog = true },
                    onPayLoan = { loan, amount -> viewModel.payLoanAmount(loan, amount) },
                    onDeleteLoan = { viewModel.deleteLoan(it) },
                    onUpdateInvestment = { inv, valNew -> viewModel.updateInvestmentValue(inv, valNew) },
                    onDeleteInvestment = { viewModel.deleteInvestment(it) }
                )
            }
        }
    }

    // --- Dialogs ---

    if (showAddTransactionDialog) {
        AddTransactionDialog(
            onDismiss = { showAddTransactionDialog = false },
            onConfirm = { amount, type, category, note ->
                viewModel.addTransaction(amount, type, category, note, System.currentTimeMillis())
                showAddTransactionDialog = false
            }
        )
    }

    if (showAddLoanDialog) {
        AddLoanDialog(
            onDismiss = { showAddLoanDialog = false },
            onConfirm = { title, amount, interestRate, type ->
                viewModel.addLoan(title, amount, interestRate, type, System.currentTimeMillis() + 2592000000) // 30 days due
                showAddLoanDialog = false
            }
        )
    }

    if (showAddInvestmentDialog) {
        AddInvestmentDialog(
            onDismiss = { showAddInvestmentDialog = false },
            onConfirm = { title, amount, returnExpected, category ->
                viewModel.addInvestment(title, amount, amount, returnExpected, category)
                showAddInvestmentDialog = false
            }
        )
    }

    if (showAddBudgetDialog) {
        AddBudgetDialog(
            onDismiss = { showAddBudgetDialog = false },
            onConfirm = { category, amount ->
                viewModel.addBudget(category, amount)
                showAddBudgetDialog = false
            }
        )
    }
}

// ==========================================
// VIEW 1: DASHBOARD
// ==========================================
@Composable
fun DashboardView(
    summary: FinancialSummary,
    isOnline: Boolean,
    onOnlineToggle: (Boolean) -> Unit,
    onManualSync: () -> Unit,
    budgets: List<Budget>,
    onAddBudgetClick: () -> Unit,
    onDeleteBudget: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Hero Banner Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Mi Salud Financiera",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSlateTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Control Mensual",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSlateText,
                    fontWeight = FontWeight.Bold
                )
            }

            // Sync simulation toggle widget
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isOnline) "Sincronización" else "Offline",
                        fontSize = 11.sp,
                        color = if (isOnline) GreenPrimary else OnSlateTextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Switch(
                        checked = isOnline,
                        onCheckedChange = onOnlineToggle,
                        modifier = Modifier
                            .testTag("connection_switch")
                            .scale(0.85f),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GreenPrimary,
                            checkedTrackColor = SlateSurfaceVariant,
                            uncheckedThumbColor = OnSlateTextSecondary,
                            uncheckedTrackColor = SlateSurfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }

        // ⚠️ ALERTA SOBRE-ENDEUDAMIENTO (OVER-INDEBTEDNESS)
        AnimatedVisibility(
            visible = summary.isOverIndebted,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = AlertCoral.copy(alpha = 0.15f)),
                border = CardDefaults.outlinedCardBorder(true).copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(AlertCoral)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("over_indebtedness_alert")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Alerta de Sobreendeudamiento",
                        tint = AlertCoral,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ALERTA DE SOBRE-ENDEUDAMIENTO",
                            color = AlertCoral,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tus deudas activas (${summary.totalBorrowed.toCurrency()}) representan el ${(summary.debtToIncomeRatio * 100).toInt()}% de tus ingresos mensuales. Se recomienda que no superen el 40%.",
                            color = OnSlateText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Balance Card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Balance Disponible",
                    color = OnSlateTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summary.balance.toCurrency(),
                    color = OnSlateText,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.testTag("total_balance_text")
                )

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = SlateSurfaceVariant, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ArrowUpward,
                                contentDescription = "Ingresos",
                                tint = IncomeGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Ingresos",
                                color = OnSlateTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = summary.totalIncome.toCurrency(),
                            color = IncomeGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("total_income_text")
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ArrowDownward,
                                contentDescription = "Gastos",
                                tint = ExpenseRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Gastos",
                                color = OnSlateTextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = summary.totalExpense.toCurrency(),
                            color = ExpenseRed,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("total_expense_text")
                        )
                    }
                }
            }
        }

        // Custom Visual Category Expense Breakdown
        if (summary.categoryExpenses.isNotEmpty()) {
            Text(
                text = "Distribución de Gastos",
                style = MaterialTheme.typography.titleMedium,
                color = OnSlateText,
                fontWeight = FontWeight.Bold
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val maxExpense = summary.categoryExpenses.values.maxOrNull() ?: 1.0

                    summary.categoryExpenses.forEach { (category, amount) ->
                        val ratio = (amount / maxExpense).toFloat().coerceIn(0f, 1f)
                        val pct = ((amount / summary.totalExpense) * 100).toInt()

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = getCategoryIcon(category),
                                        contentDescription = null,
                                        tint = GreenPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = category,
                                        color = OnSlateText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = "${amount.toCurrency()} ($pct%)",
                                    color = OnSlateTextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Bar layout
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SlateSurfaceVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(ratio)
                                        .height(8.dp)
                                        .background(GreenPrimary)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 🚨 ALERTAS DE LÍMITES DE PRESUPUESTO PERSONALIZADOS
        Text(
            text = "Alertas de Presupuesto",
            style = MaterialTheme.typography.titleMedium,
            color = OnSlateText,
            fontWeight = FontWeight.Bold
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Límites Establecidos",
                        color = OnSlateTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    TextButton(
                        onClick = onAddBudgetClick,
                        modifier = Modifier.testTag("btn_add_budget")
                    ) {
                        Text("+ Definir Límite", color = GreenPrimary, fontSize = 12.sp)
                    }
                }

                if (budgets.isEmpty()) {
                    Text(
                        text = "No has definido límites de presupuesto aún. ¡Define límites para evitar sobregasto!",
                        color = OnSlateTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                } else {
                    budgets.forEach { budget ->
                        val spent = summary.categoryExpenses[budget.category] ?: 0.0
                        val isExceeded = spent > budget.limitAmount
                        val progress = (spent / budget.limitAmount).toFloat().coerceIn(0f, 1f)

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isExceeded) AlertCoral.copy(alpha = 0.05f) else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isExceeded) Icons.Filled.Warning else Icons.Filled.TaskAlt,
                                        contentDescription = if (isExceeded) "Presupuesto Excedido" else "Presupuesto OK",
                                        tint = if (isExceeded) AlertCoral else IncomeGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = budget.category,
                                        color = OnSlateText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteBudget(budget.category) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Borrar presupuesto",
                                        tint = OnSlateTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Gastado: ${spent.toCurrency()} / Límite: ${budget.limitAmount.toCurrency()}",
                                    fontSize = 11.sp,
                                    color = if (isExceeded) AlertCoral else OnSlateTextSecondary
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    fontSize = 11.sp,
                                    color = if (isExceeded) AlertCoral else OnSlateTextSecondary,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (isExceeded) AlertCoral else IncomeGreen,
                                trackColor = SlateSurfaceVariant
                            )

                            if (isExceeded) {
                                Text(
                                    text = "⚠️ Has excedido este presupuesto por ${(spent - budget.limitAmount).toCurrency()}",
                                    fontSize = 11.sp,
                                    color = AlertCoral,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// VIEW 2: TRANSACTIONS LIST
// ==========================================
@Composable
fun TransactionsView(
    transactions: List<Transaction>,
    onDeleteTransaction: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Historial de Transacciones",
            style = MaterialTheme.typography.titleMedium,
            color = OnSlateText,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Registros locales offline y sincronizados",
            style = MaterialTheme.typography.bodySmall,
            color = OnSlateTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (transactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = null,
                        tint = OnSlateTextSecondary.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aún no hay transacciones",
                        color = OnSlateText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Toca el botón '+' para agregar ingresos o gastos.",
                        color = OnSlateTextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.testTag("transactions_list")
            ) {
                items(transactions) { tx ->
                    TransactionItem(
                        transaction = tx,
                        onDeleteClick = { onDeleteTransaction(tx.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDeleteClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circular icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.type == "INCOME") IncomeGreen.copy(alpha = 0.15f)
                            else ExpenseRed.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getCategoryIcon(transaction.category),
                        contentDescription = transaction.category,
                        tint = if (transaction.type == "INCOME") IncomeGreen else ExpenseRed,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.note.ifEmpty { transaction.category },
                        color = OnSlateText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = transaction.category,
                            color = OnSlateTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "•  ${transaction.date.toDateString()}",
                            color = OnSlateTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = if (transaction.type == "INCOME") "+${transaction.amount.toCurrency()}" else "-${transaction.amount.toCurrency()}",
                        color = if (transaction.type == "INCOME") IncomeGreen else ExpenseRed,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    // Visual Sync Indicator
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (transaction.isSynced) Icons.Filled.CloudDone else Icons.Filled.CloudQueue,
                            contentDescription = null,
                            tint = if (transaction.isSynced) IncomeGreen.copy(alpha = 0.6f) else InvestmentGold,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (transaction.isSynced) "Nube" else "Local",
                            fontSize = 9.sp,
                            color = if (transaction.isSynced) OnSlateTextSecondary else InvestmentGold
                        )
                    }
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.testTag("delete_tx_btn_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Borrar Transacción",
                        tint = AlertCoral.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// VIEW 3: LOANS AND INVESTMENTS
// ==========================================
@Composable
fun FinanceProductsView(
    loans: List<Loan>,
    investments: List<Investment>,
    onAddLoanClick: () -> Unit,
    onAddInvestmentClick: () -> Unit,
    onPayLoan: (Loan, Double) -> Unit,
    onDeleteLoan: (Long) -> Unit,
    onUpdateInvestment: (Investment, Double) -> Unit,
    onDeleteInvestment: (Long) -> Unit
) {
    val scrollState = rememberScrollState()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Loans (Préstamos), 1: Investments (Inversiones)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toggle Sub Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { activeSubTab = 0 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == 0) GreenPrimary else SlateSurface
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_loans_subtab")
            ) {
                Text(
                    text = "Préstamos / Deudas",
                    color = if (activeSubTab == 0) SlateBackground else OnSlateText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = { activeSubTab = 1 },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeSubTab == 1) GreenPrimary else SlateSurface
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_investments_subtab")
            ) {
                Text(
                    text = "Inversiones",
                    color = if (activeSubTab == 1) SlateBackground else OnSlateText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        if (activeSubTab == 0) {
            // Loans/Deudas UI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Préstamos Activos",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSlateText,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onAddLoanClick) {
                    Text("+ Registrar", color = GreenPrimary)
                }
            }

            if (loans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No posees deudas ni préstamos registrados.",
                        color = OnSlateTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(loans) { loan ->
                        LoanItem(
                            loan = loan,
                            onPayClick = { onPayLoan(loan, it) },
                            onDeleteClick = { onDeleteLoan(loan.id) }
                        )
                    }
                }
            }
        } else {
            // Investments UI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Portafolio de Inversión",
                    style = MaterialTheme.typography.titleMedium,
                    color = OnSlateText,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onAddInvestmentClick) {
                    Text("+ Registrar", color = GreenPrimary)
                }
            }

            if (investments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aún no tienes inversiones registradas.",
                        color = OnSlateTextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(investments) { inv ->
                        InvestmentItem(
                            investment = inv,
                            onUpdateValue = { onUpdateInvestment(inv, it) },
                            onDeleteClick = { onDeleteInvestment(inv.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LoanItem(
    loan: Loan,
    onPayClick: (Double) -> Unit,
    onDeleteClick: () -> Unit
) {
    var showPaymentField by remember { mutableStateOf(false) }
    var payAmountText by remember { mutableStateOf("") }

    val isBorrowed = loan.type == "BORROWED" // Debemos nosotros

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isBorrowed) ExpenseRed.copy(alpha = 0.15f)
                                else IncomeGreen.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBorrowed) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                            contentDescription = null,
                            tint = if (isBorrowed) ExpenseRed else IncomeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = loan.title,
                            color = OnSlateText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isBorrowed) "Deuda Pendiente" else "Dinero Prestado",
                            color = if (isBorrowed) ExpenseRed else IncomeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Borrar préstamo",
                        tint = AlertCoral.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Restante", color = OnSlateTextSecondary, fontSize = 11.sp)
                    Text(
                        text = loan.remainingAmount.toCurrency(),
                        color = OnSlateText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Monto Total", color = OnSlateTextSecondary, fontSize = 11.sp)
                    Text(
                        text = loan.amount.toCurrency(),
                        color = OnSlateTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tasa de Interés: ${loan.interestRate}%",
                    color = OnSlateTextSecondary,
                    fontSize = 11.sp
                )

                if (loan.remainingAmount > 0 && isBorrowed) {
                    Button(
                        onClick = { showPaymentField = !showPaymentField },
                        colors = ButtonDefaults.buttonColors(containerColor = SlateSurfaceVariant),
                        contentPadding = ButtonDefaults.ContentPadding,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Abonar Pago", color = GreenPrimary, fontSize = 11.sp)
                    }
                }
            }

            if (showPaymentField) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = payAmountText,
                        onValueChange = { payAmountText = it },
                        label = { Text("Monto del Abono", color = OnSlateTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = SlateSurfaceVariant,
                            focusedTextColor = OnSlateText,
                            unfocusedTextColor = OnSlateText
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = payAmountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                onPayClick(amt)
                                payAmountText = ""
                                showPaymentField = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(text = "Aceptar", color = SlateBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun InvestmentItem(
    investment: Investment,
    onUpdateValue: (Double) -> Unit,
    onDeleteClick: () -> Unit
) {
    var showUpdateField by remember { mutableStateOf(false) }
    var updateAmountText by remember { mutableStateOf("") }

    val gain = investment.currentValue - investment.amount
    val isProfit = gain >= 0
    val roi = (gain / investment.amount) * 100

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(InvestmentGold.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShowChart,
                            contentDescription = null,
                            tint = InvestmentGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = investment.title,
                            color = OnSlateText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = investment.category,
                            color = OnSlateTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Borrar inversión",
                        tint = AlertCoral.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Valor Actual", color = OnSlateTextSecondary, fontSize = 11.sp)
                    Text(
                        text = investment.currentValue.toCurrency(),
                        color = OnSlateText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Rendimiento (ROI)", color = OnSlateTextSecondary, fontSize = 11.sp)
                    Text(
                        text = "${if (isProfit) "+" else ""}${roi.toInt()}% (${gain.toCurrency()})",
                        color = if (isProfit) IncomeGreen else ExpenseRed,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inversión Inicial: ${investment.amount.toCurrency()}",
                    color = OnSlateTextSecondary,
                    fontSize = 11.sp
                )

                Button(
                    onClick = { showUpdateField = !showUpdateField },
                    colors = ButtonDefaults.buttonColors(containerColor = SlateSurfaceVariant),
                    contentPadding = ButtonDefaults.ContentPadding,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Actualizar Valor", color = GreenPrimary, fontSize = 11.sp)
                }
            }

            if (showUpdateField) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = updateAmountText,
                        onValueChange = { updateAmountText = it },
                        label = { Text("Nuevo Valor de Mercado", color = OnSlateTextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = SlateSurfaceVariant,
                            focusedTextColor = OnSlateText,
                            unfocusedTextColor = OnSlateText
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val valNew = updateAmountText.toDoubleOrNull() ?: 0.0
                            if (valNew > 0) {
                                onUpdateValue(valNew)
                                updateAmountText = ""
                                showUpdateField = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text(text = "Guardar", color = SlateBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ==========================================
// FORM DIALOGS
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double, String, String, String) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") } // "EXPENSE" or "INCOME"
    var category by remember { mutableStateOf("Alimentos") }

    val categories = if (type == "INCOME") {
        listOf("Sueldo", "Ventas", "Inversiones", "Préstamos", "Otros")
    } else {
        listOf("Alimentos", "Transporte", "Servicios", "Entretenimiento", "Inversiones", "Préstamos", "Otros")
    }

    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Registrar Movimiento",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSlateText
                )

                // Toggle type
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateSurfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (type == "EXPENSE") ExpenseRed else Color.Transparent)
                            .clickable {
                                type = "EXPENSE"
                                category = "Alimentos"
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Gasto",
                            color = if (type == "EXPENSE") SlateBackground else OnSlateText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (type == "INCOME") IncomeGreen else Color.Transparent)
                            .clickable {
                                type = "INCOME"
                                category = "Sueldo"
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Ingreso",
                            color = if (type == "INCOME") SlateBackground else OnSlateText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Monto ($)", color = OnSlateTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SlateSurfaceVariant,
                        focusedTextColor = OnSlateText,
                        unfocusedTextColor = OnSlateText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_transaction_amount")
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Categoría", color = OnSlateTextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = SlateSurfaceVariant,
                            focusedTextColor = OnSlateText,
                            unfocusedTextColor = OnSlateText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SlateSurface)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = OnSlateText) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota / Descripción", color = OnSlateTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SlateSurfaceVariant,
                        focusedTextColor = OnSlateText,
                        unfocusedTextColor = OnSlateText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_transaction_note")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = OnSlateTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amount = amountStr.toDoubleOrNull() ?: 0.0
                            if (amount > 0) {
                                onConfirm(amount, type, category, note)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("Guardar", color = SlateBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddLoanDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var interestStr by remember { mutableStateOf("0") }
    var type by remember { mutableStateOf("BORROWED") } // BORROWED: debemos nosotros, LENT: prestamos nosotros

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Registrar Préstamo o Deuda",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSlateText
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SlateSurfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (type == "BORROWED") ExpenseRed else Color.Transparent)
                            .clickable { type = "BORROWED" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Recibido (Debo)",
                            color = if (type == "BORROWED") SlateBackground else OnSlateText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (type == "LENT") IncomeGreen else Color.Transparent)
                            .clickable { type = "LENT" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Prestado (Me deben)",
                            color = if (type == "LENT") SlateBackground else OnSlateText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título / Nombre", color = OnSlateTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SlateSurfaceVariant,
                        focusedTextColor = OnSlateText,
                        unfocusedTextColor = OnSlateText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_loan_title")
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Monto ($)", color = OnSlateTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SlateSurfaceVariant,
                        focusedTextColor = OnSlateText,
                        unfocusedTextColor = OnSlateText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_loan_amount")
                )

                OutlinedTextField(
                    value = interestStr,
                    onValueChange = { interestStr = it },
                    label = { Text("Tasa de Interés Anual (%)", color = OnSlateTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SlateSurfaceVariant,
                        focusedTextColor = OnSlateText,
                        unfocusedTextColor = OnSlateText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = OnSlateTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amount = amountStr.toDoubleOrNull() ?: 0.0
                            val interest = interestStr.toDoubleOrNull() ?: 0.0
                            if (title.isNotEmpty() && amount > 0) {
                                onConfirm(title, amount, interest, type)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("Registrar", color = SlateBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvestmentDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Double, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var expectedReturnStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Acciones") }

    val categories = listOf("Acciones", "Criptomonedas", "Bienes Raíces", "Plazo Fijo", "Otros")
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Registrar Nueva Inversión",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSlateText
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título / Activo", color = OnSlateTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SlateSurfaceVariant,
                        focusedTextColor = OnSlateText,
                        unfocusedTextColor = OnSlateText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_inv_title")
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Monto Inicial ($)", color = OnSlateTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SlateSurfaceVariant,
                        focusedTextColor = OnSlateText,
                        unfocusedTextColor = OnSlateText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_inv_amount")
                )

                OutlinedTextField(
                    value = expectedReturnStr,
                    onValueChange = { expectedReturnStr = it },
                    label = { Text("Retorno Estimado (%)", color = OnSlateTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SlateSurfaceVariant,
                        focusedTextColor = OnSlateText,
                        unfocusedTextColor = OnSlateText
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Tipo de Activo", color = OnSlateTextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = SlateSurfaceVariant,
                            focusedTextColor = OnSlateText,
                            unfocusedTextColor = OnSlateText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SlateSurface)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = OnSlateText) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = OnSlateTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amount = amountStr.toDoubleOrNull() ?: 0.0
                            val ret = expectedReturnStr.toDoubleOrNull() ?: 0.0
                            if (title.isNotEmpty() && amount > 0) {
                                onConfirm(title, amount, ret, category)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("Registrar", color = SlateBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double) -> Unit
) {
    var category by remember { mutableStateOf("Alimentos") }
    var amountStr by remember { mutableStateOf("") }

    val categories = listOf("Alimentos", "Transporte", "Servicios", "Entretenimiento", "Inversiones", "Préstamos", "Otros")
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Definir Límite de Gasto",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = OnSlateText
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = category,
                        onValueChange = {},
                        label = { Text("Categoría", color = OnSlateTextSecondary) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = SlateSurfaceVariant,
                            focusedTextColor = OnSlateText,
                            unfocusedTextColor = OnSlateText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(SlateSurface)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat, color = OnSlateText) },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Límite de Presupuesto ($)", color = OnSlateTextSecondary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = SlateSurfaceVariant,
                        focusedTextColor = OnSlateText,
                        unfocusedTextColor = OnSlateText
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_budget_amount")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar", color = OnSlateTextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val limit = amountStr.toDoubleOrNull() ?: 0.0
                            if (limit > 0) {
                                onConfirm(category, limit)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text("Confirmar", color = SlateBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// Helper category to icon mapping
fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase(Locale.ROOT)) {
        "alimentos" -> Icons.Filled.Fastfood
        "transporte" -> Icons.Filled.LocalGasStation
        "servicios" -> Icons.Filled.Payments
        "entretenimiento" -> Icons.Filled.Movie
        "inversiones" -> Icons.Filled.ShowChart
        "préstamos" -> Icons.Filled.LocalAtm
        "sueldo" -> Icons.Filled.Work
        "ventas" -> Icons.Filled.LocalAtm
        else -> Icons.Filled.Category
    }
}

// Scale helper used inline via standard androidx.compose.ui.draw.scale

