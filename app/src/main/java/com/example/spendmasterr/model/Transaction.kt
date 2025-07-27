package com.example.spendmasterr.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Parcelize
data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val amount: Double,
    val description: String,
    val type: TransactionType,
    val category: String,
    val date: Date = Date(),
    val isRecurring: Boolean = false,
    val recurringPeriod: Int? = null // in days, null if not recurring
) : Parcelable {
    companion object {
        const val FOOD = "Food"
        const val TRANSPORT = "Transport"
        const val BILLS = "Bills"
        const val ENTERTAINMENT = "Entertainment"
        const val OTHER = "Other"
    }
} 