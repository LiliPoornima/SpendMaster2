package com.example.spendmasterr.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class BudgetEntity(
    @PrimaryKey val id: Int = 1, // Always a single row
    val amount: Double
) 