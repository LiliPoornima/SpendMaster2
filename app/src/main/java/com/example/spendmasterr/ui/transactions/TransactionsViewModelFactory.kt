package com.example.spendmasterr.ui.transactions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TransactionsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionsViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 