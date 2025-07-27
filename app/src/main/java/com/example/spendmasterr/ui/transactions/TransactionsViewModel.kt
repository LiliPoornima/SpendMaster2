package com.example.spendmasterr.ui.transactions

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.R
import com.example.spendmasterr.model.Transaction
import com.example.spendmasterr.util.TransactionPrefsManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TransactionsViewModel(
    private val context: Context
) : ViewModel() {
    private val prefsManager = TransactionPrefsManager(context)
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        _transactions.value = prefsManager.getTransactions().sortedByDescending { it.date }
    }

    fun addTransaction(transaction: Transaction) {
        prefsManager.addTransaction(transaction)
        loadTransactions()
    }

    fun updateTransaction(transaction: Transaction) {
        prefsManager.updateTransaction(transaction)
        loadTransactions()
    }

    fun deleteTransaction(transaction: Transaction) {
        prefsManager.deleteTransaction(transaction.id)
        loadTransactions()
    }

    fun getCategories(): List<String> {
        return try {
            context.resources.getStringArray(R.array.transaction_categories).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }
} 