package com.example.spendmasterr.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.data.repository.TransactionRepository
import com.example.spendmasterr.model.Transaction
import com.example.spendmasterr.model.TransactionType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val _totalBalance = MutableLiveData<Double>()
    val totalBalance: LiveData<Double> = _totalBalance

    private val _totalIncome = MutableLiveData<Double>()
    val totalIncome: LiveData<Double> = _totalIncome

    private val _totalExpense = MutableLiveData<Double>()
    val totalExpense: LiveData<Double> = _totalExpense

    private val _categorySpending = MutableLiveData<Map<String, Double>>()
    val categorySpending: LiveData<Map<String, Double>> = _categorySpending

    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> = _transactions

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.getAllTransactions().collectLatest { transactions ->
                    _transactions.value = transactions
                    calculateTotals(transactions)
                    calculateCategorySpending(transactions)
                }
            } catch (e: Exception) {
                _error.value = "Failed to load dashboard data: ${e.message}"
                resetData()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Force refresh all data
    fun refreshData() {
        loadDashboardData()
    }

    private fun resetData() {
        _transactions.value = emptyList()
        _totalBalance.value = 0.0
        _totalIncome.value = 0.0
        _totalExpense.value = 0.0
        _categorySpending.value = emptyMap()
    }

    private fun calculateTotals(transactions: List<Transaction>) {
        val income = transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expense = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        _totalIncome.value = income
        _totalExpense.value = expense
        _totalBalance.value = income - expense
    }

    private fun calculateCategorySpending(transactions: List<Transaction>) {
        val spending = transactions
            .groupBy { "${it.type}: ${it.category}" }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }
            .toMap()

        _categorySpending.value = spending
    }
} 