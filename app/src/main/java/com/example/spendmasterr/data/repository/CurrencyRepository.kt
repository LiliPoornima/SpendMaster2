package com.example.spendmasterr.data.repository

import com.example.spendmasterr.data.database.CurrencyDao
import com.example.spendmasterr.data.database.CurrencyEntity

class CurrencyRepository(private val currencyDao: CurrencyDao) {
    suspend fun getCurrency(): String {
        return currencyDao.getCurrency()?.code ?: "USD"
    }

    suspend fun setCurrency(code: String) {
        currencyDao.insertOrUpdateCurrency(CurrencyEntity(code = code))
    }
} 