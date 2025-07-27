// TODO: Remove this file if not used, or refactor to use Room-based repository.

package com.example.spendmasterr.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spendmasterr.data.repository.TransactionRepository
import com.example.spendmasterr.model.Transaction
import com.example.spendmasterr.model.TransactionType
import java.util.Date
import java.util.UUID
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AddTransactionViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _saveResult = MutableLiveData<SaveResult>()
    val saveResult: LiveData<SaveResult> = _saveResult

    fun addTransaction(description: String, amount: Double, category: String, type: TransactionType, date: Date) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    amount = amount,
                    description = description,
                    type = type,
                    category = category,
                    date = date,
                    isRecurring = false,
                    recurringPeriod = null
                )
                repository.addTransaction(transaction)
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

class AddTransactionViewModelFactory(private val repository: TransactionRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 