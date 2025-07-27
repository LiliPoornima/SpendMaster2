package com.example.spendmasterr.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class BudgetPrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "monthly_budget"

    fun saveBudget(amount: Double) {
        prefs.edit().putFloat(key, amount.toFloat()).apply()
    }

    fun getBudget(): Double {
        return prefs.getFloat(key, 0.0f).toDouble()
    }

    fun clearBudget() {
        prefs.edit().remove(key).apply()
    }
} 