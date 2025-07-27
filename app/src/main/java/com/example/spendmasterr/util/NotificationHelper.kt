package com.example.spendmasterr.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.spendmasterr.ui.MainActivity
import com.example.spendmasterr.R
import java.text.NumberFormat
import java.util.Locale
import com.example.spendmasterr.notification.BudgetNotificationManager
import kotlinx.coroutines.*
import android.util.Log
import android.widget.Toast

class NotificationHelper(private val context: Context) {
    companion object {
        const val CHANNEL_ID = "budget_alerts_test"
        const val NOTIFICATION_ID = 1
    }

    private val budgetNotificationManager = BudgetNotificationManager(context)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Budget Alerts"
                val descriptionText = "Notifications for budget alerts"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Call this from a coroutine or background thread.
     */
    suspend fun showBudgetNotification(monthlyBudget: Double) {
        Log.d("NOTIF_TEST", "Attempting to show notification")
        // Always show a notification when called (for testing)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Budget Set!")
            .setContentText("Your monthly budget is set to $${String.format("%.2f", monthlyBudget)}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(1001, notification)
    }

    private suspend fun getMonthlyExpenses(): Double {
        var expenses = 0.0
        // Refactor all usages of TransactionRepository to use TransactionPrefsManager instead.
        // This function is no longer needed as TransactionRepository is removed.
        // Keeping it for now as it might be re-introduced or refactored later.
        // For now, it will return 0.0 as TransactionRepository is not available.
        return expenses
    }

    fun showBudgetAlert(monthlyBudget: Double, monthlyExpenses: Double) {
        try {
            val progress = if (monthlyBudget > 0) {
                (monthlyExpenses / monthlyBudget * 100).toInt()
            } else {
                0
            }

            val title = when {
                progress >= 100 -> "Budget Exceeded!"
                progress >= 90 -> "Budget Warning!"
                progress >= 70 -> "Budget Alert!"
                else -> return // Don't show notification if below 70%
            }

            val remaining = monthlyBudget - monthlyExpenses
            val message = when {
                progress >= 100 -> "You've exceeded your budget by ${formatCurrency(monthlyExpenses - monthlyBudget)}"
                else -> "You have ${formatCurrency(remaining)} remaining (${100 - progress}% left)"
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showBudgetNotification(title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun formatCurrency(amount: Double): String {
        return try {
            NumberFormat.getCurrencyInstance(Locale.getDefault()).format(amount)
        } catch (e: Exception) {
            e.printStackTrace()
            "$0.00"
        }
    }
} 