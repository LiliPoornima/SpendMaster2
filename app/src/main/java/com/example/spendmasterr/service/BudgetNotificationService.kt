package com.example.spendmasterr.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.spendmasterr.R
import com.example.spendmasterr.MainActivity
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import com.example.spendmasterr.data.database.SpendMasterDatabase
import com.example.spendmasterr.data.repository.BudgetRepository
import com.example.spendmasterr.data.repository.TransactionRepository
import kotlinx.coroutines.*

class BudgetNotificationService : Service() {
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private lateinit var notificationManager: NotificationManager
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private const val CHANNEL_ID = "budget_alerts"
        private const val ALERT_CHANNEL_ID = "budget_warnings"
        private const val NOTIFICATION_ID = 1001
        private const val FOREGROUND_NOTIFICATION_ID = 1002
        private const val CHECK_INTERVAL_HOURS = 6L
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val notification = createNotification(
                "Budget Monitoring",
                "SpendMaster is monitoring your budget",
                true
            )
            startForeground(FOREGROUND_NOTIFICATION_ID, notification)
            // Check budget status immediately and start monitoring
            checkBudgetStatus()
            startBudgetMonitoring()
            return START_STICKY
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
            return START_NOT_STICKY
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startBudgetMonitoring() {
        try {
            executor.scheduleAtFixedRate({
                checkBudgetStatus()
            }, CHECK_INTERVAL_HOURS, CHECK_INTERVAL_HOURS, TimeUnit.HOURS)
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
    }

    private fun checkBudgetStatus() {
        serviceScope.launch {
            try {
                val db = SpendMasterDatabase.getDatabase(this@BudgetNotificationService)
                val budgetRepository = BudgetRepository(db.budgetDao())
                val transactionRepository = TransactionRepository(db.transactionDao())
                val monthlyBudget = budgetRepository.getBudget()
                if (monthlyBudget <= 0) return@launch
                val now = java.util.Date()
                val calendar = java.util.Calendar.getInstance()
                calendar.time = now
                calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)
                val startOfMonth = calendar.time
                var monthlyExpenses = 0.0
                transactionRepository.getAllTransactions().collect { transactions ->
                    monthlyExpenses = transactions.filter { it.type.name == "EXPENSE" && it.date >= startOfMonth && it.date <= now }
                        .sumOf { it.amount }
                }
                val percentageUsed = (monthlyExpenses / monthlyBudget * 100).toInt()
                when {
                    percentageUsed >= 100 -> showBudgetAlert(
                        "Budget Exceeded!",
                        "You've exceeded your monthly budget by ${formatAmount(monthlyExpenses - monthlyBudget)}",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    percentageUsed >= 90 -> showBudgetAlert(
                        "Budget Warning!",
                        "You've used ${percentageUsed}% of your monthly budget. Only ${formatAmount(monthlyBudget - monthlyExpenses)} remaining.",
                        NotificationManager.IMPORTANCE_HIGH
                    )
                    percentageUsed >= 70 -> showBudgetAlert(
                        "Budget Alert!",
                        "You've used ${percentageUsed}% of your monthly budget. ${formatAmount(monthlyBudget - monthlyExpenses)} remaining.",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                // Channel for service notification
                val serviceChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Budget Service",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Ongoing budget monitoring service"
                    setShowBadge(false)
                }

                // Channel for budget alerts
                val alertChannel = NotificationChannel(
                    ALERT_CHANNEL_ID,
                    "Budget Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Important budget alerts and warnings"
                    enableLights(true)
                    enableVibration(true)
                    setShowBadge(true)
                }

                notificationManager.createNotificationChannels(listOf(serviceChannel, alertChannel))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotification(
        title: String,
        message: String,
        isForeground: Boolean = false
    ): android.app.Notification {
        try {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            
            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                pendingIntentFlags
            )

            val channelId = if (isForeground) CHANNEL_ID else ALERT_CHANNEL_ID
            
            return NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(if (isForeground) NotificationCompat.PRIORITY_LOW else NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(!isForeground)
                .setOngoing(isForeground)
                .setContentIntent(pendingIntent)
                .apply {
                    if (!isForeground) {
                        setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        setVibrate(longArrayOf(0, 500, 250, 500))
                    }
                }
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            // Return a basic notification if there's an error
            return NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Budget Monitoring")
                .setContentText("Service is running")
                .build()
        }
    }

    private fun showBudgetAlert(title: String, message: String, importance: Int) {
        try {
            // Update channel importance if needed
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = notificationManager.getNotificationChannel(ALERT_CHANNEL_ID)
                if (channel.importance != importance) {
                    channel.importance = importance
                    notificationManager.createNotificationChannel(channel)
                }
            }

            val notification = createNotification(title, message)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun formatAmount(amount: Double): String {
        return try {
            val format = java.text.NumberFormat.getCurrencyInstance()
            format.format(amount)
        } catch (e: Exception) {
            "%.2f".format(amount)
        }
    }

    override fun onDestroy() {
        try {
            executor.shutdown()
            serviceScope.cancel()
            super.onDestroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 