package com.example.spendmasterr.util

import android.content.Context
import android.content.SharedPreferences

class CurrencyPrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("currency_prefs", Context.MODE_PRIVATE)
    private val key = "selected_currency"

    fun saveCurrency(currencyCode: String) {
        prefs.edit().putString(key, currencyCode).apply()
    }

    fun getCurrency(): String {
        return prefs.getString(key, "USD") ?: "USD"
    }

    fun clearCurrency() {
        prefs.edit().remove(key).apply()
    }
} 