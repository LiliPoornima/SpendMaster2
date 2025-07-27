package com.example.spendmasterr.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.spendmasterr.MainActivity
import com.example.spendmasterr.R

class BudgetNotificationManager(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showBudgetWarning(remainingBudget: Double, totalBudget: Double) {
        val percentage = (remainingBudget / totalBudget) * 100
        val title = if (percentage <= 20) {
            "Budget Warning!"
        } else {
            "Budget Alert"
        }
        
        val message = if (percentage <= 20) {
            "Your budget is running low! Only ${String.format("%.2f", percentage)}% remaining."
        } else {
            "You have ${String.format("%.2f", percentage)}% of your budget remaining."
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

        val notification = NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
} 