package com.example.spendmasterr.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.data.repository.BudgetRepository
import com.example.spendmasterr.data.repository.CurrencyRepository
import com.example.spendmasterr.data.repository.TransactionRepository
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val repository: BudgetRepository,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {
    private val _budget = MutableLiveData<Double>(0.0)
    val budget: LiveData<Double> = _budget

    private val _currencyCode = MutableLiveData<String>("USD")
    val currencyCode: LiveData<String> = _currencyCode

    init {
        loadBudget()
        loadCurrency()
    }

    private fun loadBudget() {
        viewModelScope.launch {
            try {
                _budget.value = repository.getBudget()
            } catch (e: Exception) {
                e.printStackTrace()
                _budget.value = 0.0
            }
        }
    }

    private fun loadCurrency() {
        viewModelScope.launch {
            _currencyCode.value = currencyRepository.getCurrency()
        }
    }

    fun updateBudget(newBudget: Double) {
        viewModelScope.launch {
            try {
                repository.setBudget(newBudget)
                _budget.value = newBudget
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getMonthlyExpenses(transactionRepository: TransactionRepository): Double {
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = calendar.time
        return transactionRepository.getTotalAmountByTypeAndDateRange(
            com.example.spendmasterr.model.TransactionType.EXPENSE,
            startOfMonth,
            now
        )
    }

    private fun formatCurrency(amount: Double): String {
        return try {
            com.example.spendmasterr.util.CurrencyFormatter.formatAmount(amount, _currencyCode.value ?: "USD")
        } catch (e: Exception) {
            e.printStackTrace()
            "$${String.format("%.2f", amount)}"
        }
    }

    class Factory(private val repository: BudgetRepository, private val currencyRepository: CurrencyRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BudgetViewModel(repository, currencyRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 