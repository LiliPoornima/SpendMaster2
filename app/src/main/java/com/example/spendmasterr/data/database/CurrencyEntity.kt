package com.example.spendmasterr.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyEntity(
    @PrimaryKey val id: Int = 1, // Always a single row
    val code: String
) 