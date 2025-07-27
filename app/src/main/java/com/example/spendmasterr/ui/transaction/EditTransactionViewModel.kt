package com.example.spendmasterr.ui.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.data.Result
import com.example.spendmasterr.data.repository.TransactionRepository
import com.example.spendmasterr.model.Transaction
import kotlinx.coroutines.launch

class EditTransactionViewModel(private val repository: TransactionRepository, private val categoriesProvider: () -> List<String>) : ViewModel() {
    private val _updateResult = MutableLiveData<Result<Unit>>()
    val updateResult: LiveData<Result<Unit>> = _updateResult

    fun getCategories(): List<String> {
        return categoriesProvider()
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
                _updateResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _updateResult.value = Result.Error(e)
            }
        }
    }
}

class EditTransactionViewModelFactory(
    private val repository: TransactionRepository,
    private val categoriesProvider: () -> List<String>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditTransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditTransactionViewModel(repository, categoriesProvider) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 