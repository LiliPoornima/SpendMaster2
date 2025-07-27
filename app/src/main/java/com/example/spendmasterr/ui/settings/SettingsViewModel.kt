package com.example.spendmasterr.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.data.repository.CurrencyRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: CurrencyRepository) : ViewModel() {
    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String> = _currency

    init {
        loadCurrency()
    }

    private fun loadCurrency() {
        viewModelScope.launch {
            _currency.value = repository.getCurrency()
        }
    }

    fun updateCurrency(newCurrency: String) {
        viewModelScope.launch {
            repository.setCurrency(newCurrency)
            _currency.value = newCurrency
        }
    }

    fun setSelectedCurrency(currency: String) {
        val currencyCode = currency.take(3)
        updateCurrency(currencyCode)
    }
} 