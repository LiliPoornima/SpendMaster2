package com.example.spendmasterr.data.repository

import com.example.spendmasterr.data.database.BudgetDao
import com.example.spendmasterr.data.database.BudgetEntity

class BudgetRepository(private val budgetDao: BudgetDao) {
    suspend fun getBudget(): Double {
        return budgetDao.getBudget()?.amount ?: 0.0
    }

    suspend fun setBudget(amount: Double) {
        budgetDao.insertOrUpdateBudget(BudgetEntity(amount = amount))
    }
} 