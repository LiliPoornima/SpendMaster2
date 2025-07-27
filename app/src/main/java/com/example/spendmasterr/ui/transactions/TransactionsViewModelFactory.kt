package com.example.spendmasterr.ui.transactions

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spendmasterr.data.database.SpendMasterDatabase
import com.example.spendmasterr.data.repository.TransactionRepository

class TransactionsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
            val db = SpendMasterDatabase.getDatabase(context)
            val repository = TransactionRepository(db.transactionDao())
            @Suppress("UNCHECKED_CAST")
            return TransactionsViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 