package com.example.spendmasterr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.spendmasterr.data.database.SpendMasterDatabase
import com.example.spendmasterr.data.repository.CurrencyRepository
import kotlinx.coroutines.launch

class SharedCurrencyViewModel(application: Application) : AndroidViewModel(application) {
    private val currencyRepository = CurrencyRepository(SpendMasterDatabase.getDatabase(application).currencyDao())
    private val _currencyCode = MutableLiveData<String>("USD")
    val currencyCode: LiveData<String> = _currencyCode

    init {
        viewModelScope.launch {
            _currencyCode.value = currencyRepository.getCurrency()
        }
    }

    fun setCurrency(newCode: String) {
        viewModelScope.launch {
            currencyRepository.setCurrency(newCode)
            _currencyCode.value = newCode
        }
    }
} 