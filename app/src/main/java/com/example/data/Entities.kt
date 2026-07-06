package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String, // e.g. "Alimentos", "Transporte", "Sueldo", "Servicios", "Inversiones", "Préstamos", "Otros"
    val note: String,
    val date: Long, // timestamp
    val isSynced: Boolean = false
)

@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val remainingAmount: Double,
    val interestRate: Double, // percentage (e.g. 12.5)
    val dueDate: Long, // timestamp
    val type: String, // "LENT" (prestado a otros), "BORROWED" (recibido de otros)
    val isSynced: Boolean = false
)

@Entity(tableName = "investments")
data class Investment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val currentValue: Double,
    val expectedReturn: Double, // percentage (e.g. 8.0)
    val category: String, // "Acciones", "Criptomonedas", "Bienes Raíces", "Plazo Fijo", "Otros"
    val date: Long, // timestamp
    val isSynced: Boolean = false
)

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey val category: String, // Primary key is the category itself to enforce one budget per category
    val limitAmount: Double,
    val isSynced: Boolean = false
)
