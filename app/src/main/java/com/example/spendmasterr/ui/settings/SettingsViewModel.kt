package com.example.spendmasterr.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.util.CurrencyPrefsManager
import kotlinx.coroutines.launch

class SettingsViewModel(private val context: android.content.Context) : ViewModel() {
    private val prefsManager = CurrencyPrefsManager(context)
    private val _currency = MutableLiveData<String>()
    val currency: LiveData<String> = _currency

    init {
        loadCurrency()
    }

    private fun loadCurrency() {
        _currency.value = prefsManager.getCurrency()
    }

    fun updateCurrency(newCurrency: String) {
        prefsManager.saveCurrency(newCurrency)
        _currency.value = newCurrency
    }

    fun setSelectedCurrency(currency: String) {
        val currencyCode = currency.take(3)
        updateCurrency(currencyCode)
    }
} 