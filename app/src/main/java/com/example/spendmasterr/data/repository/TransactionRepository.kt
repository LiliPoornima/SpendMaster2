package com.example.spendmasterr.data.repository

import com.example.spendmasterr.data.database.TransactionDao
import com.example.spendmasterr.data.database.TransactionEntity
import com.example.spendmasterr.model.Transaction
import com.example.spendmasterr.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID

class TransactionRepository(private val transactionDao: TransactionDao) {

    fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionDao.getAllTransactions().map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getTransactionsByType(type: TransactionType): Flow<List<Transaction>> {
        return transactionDao.getTransactionsByType(type.name).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    fun getTransactionsBetweenDates(startDate: Date, endDate: Date): Flow<List<Transaction>> {
        return transactionDao.getTransactionsBetweenDates(startDate.time, endDate.time).map { entities ->
            entities.map { it.toTransaction() }
        }
    }

    suspend fun getTotalAmountByTypeAndDateRange(type: TransactionType, startDate: Date, endDate: Date): Double {
        return transactionDao.getTotalAmountByTypeAndDateRange(type.name, startDate.time, endDate.time) ?: 0.0
    }

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        transactionDao.deleteTransaction(transaction.toEntity())
    }

    private fun TransactionEntity.toTransaction(): Transaction {
        return Transaction(
            id = id,
            amount = amount,
            description = description,
            type = TransactionType.valueOf(type),
            category = category,
            date = date,
            isRecurring = isRecurring,
            recurringPeriod = recurringPeriod
        )
    }

    private fun Transaction.toEntity(): TransactionEntity {
        return TransactionEntity(
            id = id,
            amount = amount,
            description = description,
            type = type.name,
            category = category,
            date = date,
            isRecurring = isRecurring,
            recurringPeriod = recurringPeriod
        )
    }
} 