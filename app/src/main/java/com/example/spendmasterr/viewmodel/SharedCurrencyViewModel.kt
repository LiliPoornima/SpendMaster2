package com.example.spendmasterr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SharedCurrencyViewModel(application: Application) : AndroidViewModel(application) {
    private val _currencyCode = MutableLiveData<String>("USD")
    val currencyCode: LiveData<String> = _currencyCode

    init {
        viewModelScope.launch {
            _currencyCode.value = "USD" // Assuming a default value or fetching from a remote source
        }
    }

    fun setCurrency(newCode: String) {
        viewModelScope.launch {
            _currencyCode.value = newCode
        }
    }
} 