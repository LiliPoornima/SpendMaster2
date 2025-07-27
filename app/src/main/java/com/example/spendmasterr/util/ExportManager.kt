package com.example.spendmasterr.util

import android.content.Context
import android.os.Environment
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log
import kotlinx.coroutines.flow.first
import java.io.FileInputStream
import org.json.JSONObject
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.spendmasterr.util.BudgetPrefsManager
import com.example.spendmasterr.util.TransactionPrefsManager

class ExportManager(private val context: Context) {
    private val gson = GsonBuilder().setPrettyPrinting().create()

    fun exportToJson(): Result<String> {
        Log.d("EXPORT_TEST", "exportToJson called")
        return try {
            val transactionRepository = TransactionPrefsManager(context)
            val budgetRepository = BudgetPrefsManager(context)
            var transactions: List<com.example.spendmasterr.model.Transaction> = emptyList()
            try {
                Log.d("EXPORT_TEST", "Before collecting transactions Flow")
                transactions = runBlocking {
                    transactionRepository.getTransactions()
                }
                Log.d("EXPORT_TEST", "Transactions loaded: ${transactions.size}")
            } catch (e: Exception) {
                Log.e("EXPORT_TEST", "Exception during Flow collection: ${e.message}", e)
                throw e
            }
            Log.d("EXPORT_TEST", "Before loading monthly budget")
            val monthlyBudget = runBlocking { budgetRepository.getBudget() }
            Log.d("EXPORT_TEST", "Monthly budget loaded: $monthlyBudget")

            Log.d("EXPORT_TEST", "Before creating exportData map")
            val exportData = mapOf(
                "transactions" to transactions,
                "monthlyBudget" to monthlyBudget,
                "exportDate" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            )
            Log.d("EXPORT_TEST", "exportData map created")

            Log.d("EXPORT_TEST", "Before creating JSON string")
            val jsonString = gson.toJson(exportData)
            Log.d("EXPORT_TEST", "JSON string created, length: ${jsonString.length}")
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "spendmaster_export_$timestamp.txt"
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            Log.d("EXPORT_TEST", "Downloads dir: ${downloadsDir.absolutePath}")
            val exportFile = File(downloadsDir, fileName)
            Log.d("EXPORT_TEST", "Export file path: ${exportFile.absolutePath}")
            Log.d("EXPORT_TEST", "Before writing file")
            exportFile.writeText(jsonString)
            Log.d("EXPORT_TEST", "Export successful: ${exportFile.absolutePath}")
            Result.success(exportFile.absolutePath)
        } catch (e: Exception) {
            Log.e("EXPORT_TEST", "Export failed: ${e.message}", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun restoreFromTxt(fileName: String): Result<String> {
        Log.d("EXPORT_TEST", "restoreFromTxt called with $fileName")
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val restoreFile = File(downloadsDir, fileName)
            if (!restoreFile.exists()) {
                Log.e("EXPORT_TEST", "Restore file does not exist: ${restoreFile.absolutePath}")
                return Result.failure(Exception("File not found"))
            }
            val jsonString = restoreFile.readText()
            Log.d("EXPORT_TEST", "Read file content, length: ${jsonString.length}")
            val json = JSONObject(jsonString)
            val transactionRepository = TransactionPrefsManager(context)
            val budgetRepository = BudgetPrefsManager(context)
            val transactionsJson = json.getJSONArray("transactions")
            val monthlyBudget = json.getDouble("monthlyBudget")
            // Restore budget
            runBlocking { budgetRepository.saveBudget(monthlyBudget) }
            Log.d("EXPORT_TEST", "Budget restored: $monthlyBudget")
            // Restore transactions
            runBlocking {
                for (i in 0 until transactionsJson.length()) {
                    val txJson = transactionsJson.getJSONObject(i)
                    val id = txJson.optString("id", java.util.UUID.randomUUID().toString())
                    val amount = txJson.getDouble("amount")
                    val description = txJson.optString("description", "")
                    val type = com.example.spendmasterr.model.TransactionType.valueOf(txJson.getString("type"))
                    val category = txJson.optString("category", "Other")
                    val dateValue = txJson.get("date")
                    val date = when (dateValue) {
                        is Number -> java.util.Date(dateValue.toLong())
                        is String -> {
                            val formats = listOf(
                                "MMM dd, yyyy h:mm:ss a",
                                "yyyy-MM-dd HH:mm:ss",
                                "yyyy-MM-dd'T'HH:mm:ss'Z'"
                            )
                            var parsedDate: java.util.Date? = null
                            for (format in formats) {
                                try {
                                    parsedDate = java.text.SimpleDateFormat(format, java.util.Locale.getDefault()).parse(dateValue)
                                    if (parsedDate != null) break
                                } catch (_: Exception) {}
                            }
                            parsedDate ?: java.util.Date()
                        }
                        else -> java.util.Date()
                    }
                    val isRecurring = txJson.optBoolean("isRecurring", false)
                    val recurringPeriod = if (txJson.has("recurringPeriod") && !txJson.isNull("recurringPeriod")) txJson.getInt("recurringPeriod") else null
                    val transaction = com.example.spendmasterr.model.Transaction(
                        id = id,
                        amount = amount,
                        description = description,
                        type = type,
                        category = category,
                        date = date,
                        isRecurring = isRecurring,
                        recurringPeriod = recurringPeriod
                    )
                    transactionRepository.addTransaction(transaction)
                }
            }
            Log.d("EXPORT_TEST", "Transactions restored: ${transactionsJson.length()}")
            Result.success("Restored ${transactionsJson.length()} transactions and budget $monthlyBudget")
        } catch (e: Exception) {
            Log.e("EXPORT_TEST", "Restore failed: ${e.message}", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }
} 