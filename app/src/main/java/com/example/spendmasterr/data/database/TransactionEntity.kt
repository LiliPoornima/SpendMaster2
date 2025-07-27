package com.example.spendmasterr.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.util.Date

@Entity(tableName = "transactions")
@TypeConverters(Converters::class)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val description: String,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String,
    val date: Date,
    val isRecurring: Boolean = false,
    val recurringPeriod: Int? = null // in days, null if not recurring
) 