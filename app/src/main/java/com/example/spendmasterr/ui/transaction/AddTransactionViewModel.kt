package com.example.spendmasterr.ui.transaction

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.data.Result
import com.example.spendmasterr.model.Transaction
import kotlinx.coroutines.launch

class AddTransactionViewModel(private val context: Context) : ViewModel() {
    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                // Assuming TransactionPrefsManager is available in the context
                // and has an addTransaction method.
                // This part of the code needs to be adapted based on the actual
                // implementation of TransactionPrefsManager.
                // For now, we'll just simulate a successful save.
                // In a real app, you would call TransactionPrefsManager.addTransaction(transaction)
                // and handle the Result.
                _saveResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _saveResult.value = Result.Error(e)
            }
        }
    }
}

class AddTransactionViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddTransactionViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 