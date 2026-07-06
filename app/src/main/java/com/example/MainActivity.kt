package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.AppDatabase
import com.example.data.FinanceRepository
import com.example.ui.FinanceAppScreen
import com.example.ui.FinanceViewModel
import com.example.ui.FinanceViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room Database & Repository
        val database = AppDatabase.getDatabase(this)
        val repository = FinanceRepository(database.financeDao())

        // Create ViewModel utilizing Factory
        val viewModel: FinanceViewModel by viewModels {
            FinanceViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                FinanceAppScreen(viewModel = viewModel)
            }
        }
    }
}
