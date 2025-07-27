package com.example.spendmasterr.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.util.TransactionPrefsManager
import com.example.spendmasterr.model.Transaction
import kotlinx.coroutines.launch

class EditTransactionViewModel(private val context: android.content.Context, private val categoriesProvider: () -> List<String>) : ViewModel() {
    private val prefsManager = TransactionPrefsManager(context)
    private val _updateResult = MutableLiveData<com.example.spendmasterr.data.Result<Unit>>()
    val updateResult: LiveData<com.example.spendmasterr.data.Result<Unit>> = _updateResult

    fun getCategories(): List<String> {
        return categoriesProvider()
    }

    fun updateTransaction(transaction: com.example.spendmasterr.model.Transaction) {
        try {
            prefsManager.updateTransaction(transaction)
            _updateResult.value = com.example.spendmasterr.data.Result.Success(Unit)
        } catch (e: Exception) {
            _updateResult.value = com.example.spendmasterr.data.Result.Error(e)
        }
    }
}

class EditTransactionViewModelFactory(
    private val context: android.content.Context,
    private val categoriesProvider: () -> List<String>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditTransactionViewModel(context, categoriesProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 