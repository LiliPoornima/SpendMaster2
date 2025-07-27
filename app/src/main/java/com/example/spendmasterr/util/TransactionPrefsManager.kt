package com.example.spendmasterr.util

import android.content.Context
import android.content.SharedPreferences
import com.example.spendmasterr.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TransactionPrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("transactions_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val key = "transactions_list"

    fun saveTransactions(transactions: List<Transaction>) {
        val json = gson.toJson(transactions)
        prefs.edit().putString(key, json).apply()
    }

    fun getTransactions(): List<Transaction> {
        val json = prefs.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun addTransaction(transaction: Transaction) {
        val transactions = getTransactions().toMutableList()
        transactions.add(transaction)
        saveTransactions(transactions)
    }

    fun updateTransaction(updated: Transaction) {
        val transactions = getTransactions().map {
            if (it.id == updated.id) updated else it
        }
        saveTransactions(transactions)
    }

    fun deleteTransaction(transactionId: String) {
        val transactions = getTransactions().filter { it.id != transactionId }
        saveTransactions(transactions)
    }
} 