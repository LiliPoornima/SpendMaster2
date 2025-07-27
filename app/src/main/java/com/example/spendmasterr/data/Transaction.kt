package com.example.spendmasterr.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val id: String,
    val title: String,
    val amount: Double,
    val category: String,
    val type: Type,
    val date: Long
) : Parcelable {
    enum class Type {
        INCOME,
        EXPENSE
    }
} 