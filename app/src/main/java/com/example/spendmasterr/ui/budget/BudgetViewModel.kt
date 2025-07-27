package com.example.spendmasterr.ui.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.util.TransactionPrefsManager
import java.util.Calendar
import java.util.Date
import kotlinx.coroutines.launch

class BudgetViewModel(
    private val context: android.content.Context
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
        val budgetPrefs = com.example.spendmasterr.util.BudgetPrefsManager(context)
        _budget.value = budgetPrefs.getBudget()
    }

    private fun loadCurrency() {
        val currencyPrefs = com.example.spendmasterr.util.CurrencyPrefsManager(context)
        _currencyCode.value = currencyPrefs.getCurrency()
    }

    fun updateBudget(newBudget: Double) {
        val budgetPrefs = com.example.spendmasterr.util.BudgetPrefsManager(context)
        budgetPrefs.saveBudget(newBudget)
        _budget.value = newBudget
    }

    fun updateCurrency(newCurrency: String) {
        val currencyPrefs = com.example.spendmasterr.util.CurrencyPrefsManager(context)
        currencyPrefs.saveCurrency(newCurrency)
        _currencyCode.value = newCurrency
    }

    fun getMonthlyExpenses(): Double {
        val transactionPrefs = com.example.spendmasterr.util.TransactionPrefsManager(context)
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val startOfMonth = calendar.time
        val transactions = transactionPrefs.getTransactions()
        return transactions.filter { it.type.name == "EXPENSE" && it.date >= startOfMonth && it.date <= now }
            .sumOf { it.amount }
    }

    private fun formatCurrency(amount: Double): String {
        return try {
            com.example.spendmasterr.util.CurrencyFormatter.formatAmount(amount, _currencyCode.value ?: "USD")
        } catch (e: Exception) {
            e.printStackTrace()
            "$${String.format("%.2f", amount)}"
        }
    }

    class Factory(private val context: android.content.Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BudgetViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
} 