// TODO: Remove this file if not used, or refactor to use Room-based repository.

package com.example.spendmasterr.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spendmasterr.model.Transaction
import com.example.spendmasterr.model.TransactionType
import java.util.Date
import java.util.UUID
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.spendmasterr.util.TransactionPrefsManager

class AddTransactionViewModel(private val context: android.content.Context) : ViewModel() {
    private val prefsManager = TransactionPrefsManager(context)
    private val _saveResult = MutableLiveData<SaveResult>()
    val saveResult: LiveData<SaveResult> = _saveResult

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                TransactionPrefsManager(context).addTransaction(transaction)
                _saveResult.value = SaveResult.Success
            } catch (e: Exception) {
                _saveResult.value = SaveResult.Error(e.message ?: "Failed to save transaction")
            }
        }
    }

    sealed class SaveResult {
        object Success : SaveResult()
        data class Error(val message: String) : SaveResult()
    }
}

class AddTransactionViewModelFactory(private val context: android.content.Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTransactionViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 