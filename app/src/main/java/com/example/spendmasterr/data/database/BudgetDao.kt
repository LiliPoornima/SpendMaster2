package com.example.spendmasterr.data.database

import androidx.room.*

@Dao
interface BudgetDao {
    @Query("SELECT * FROM budget WHERE id = 1 LIMIT 1")
    suspend fun getBudget(): BudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBudget(budget: BudgetEntity)
} 