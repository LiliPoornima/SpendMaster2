package com.example.spendmasterr.data.database

import androidx.room.*

@Dao
interface CurrencyDao {
    @Query("SELECT * FROM currency WHERE id = 1 LIMIT 1")
    suspend fun getCurrency(): CurrencyEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCurrency(currency: CurrencyEntity)
} 