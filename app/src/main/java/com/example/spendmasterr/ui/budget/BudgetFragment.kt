package com.example.spendmasterr.ui.budget

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.spendmasterr.R
import com.example.spendmasterr.databinding.FragmentBudgetBinding
import com.example.spendmasterr.service.BudgetNotificationService
import com.example.spendmasterr.util.CurrencyFormatter
import com.example.spendmasterr.util.NotificationHelper
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.launch
import androidx.core.app.NotificationCompat
import android.app.NotificationManager
import android.content.Context

class BudgetFragment : Fragment() {
    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BudgetViewModel
    private lateinit var notificationHelper: NotificationHelper
    private var currentCurrencyCode: String = "USD"

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        try {
            _binding = FragmentBudgetBinding.inflate(inflater, container, false)
            viewModel = ViewModelProvider(
                this,
                BudgetViewModel.Factory(requireContext())
            )[BudgetViewModel::class.java]
            notificationHelper = NotificationHelper(requireContext())

            viewModel.currencyCode.observe(viewLifecycleOwner) { code ->
                currentCurrencyCode = code
                binding.etMonthlyBudget.hint = getString(R.string.hint_monthly_budget) + " (" + currentCurrencyCode + ")"
                updateBudgetProgress()
                updateBarChart()
            }

            viewModel.budget.observe(viewLifecycleOwner) { budget ->
                binding.etMonthlyBudget.setText(budget.toString())
                updateBudgetProgress()
            }

            setupUI()
            setupClickListeners()
            observeViewModel()
            setupBarChart()
            checkNotificationPermission()

            return binding.root
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error initializing budget screen", Toast.LENGTH_SHORT).show()
            return binding.root
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            updateBudgetProgress()
            updateBarChart()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        try {
            viewModel.budget.observe(viewLifecycleOwner) { budget ->
                binding.etMonthlyBudget.setText(budget.toString())
            updateBudgetProgress()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.etMonthlyBudget.setText("0")
        }
    }

    private fun setupClickListeners() {
        binding.btnSaveBudget.setOnClickListener {
            saveBudget()
        }
    }

    private fun observeViewModel() {
        viewModel.budget.observe(viewLifecycleOwner) { budget ->
            try {
                binding.etMonthlyBudget.setText(budget.toString())
                updateBudgetProgress()
                updateBarChart()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveBudget() {
        Toast.makeText(requireContext(), "saveBudget() called", Toast.LENGTH_SHORT).show()
        try {
            val budget = binding.etMonthlyBudget.text.toString().toDouble()
            if (budget < 0) {
                Toast.makeText(requireContext(), "Budget cannot be negative", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.updateBudget(budget)
            // Show in-app notification using Toast
            Toast.makeText(requireContext(), "Budget updated! Your monthly budget is set to $${String.format("%.2f", budget)}", Toast.LENGTH_LONG).show()
            // Show the real budget notification after saving
            viewLifecycleOwner.lifecycleScope.launch {
                notificationHelper.showBudgetNotification(budget)
                // Check and notify for budget thresholds
                val monthlyExpenses = viewModel.getMonthlyExpenses()
                notificationHelper.showBudgetAlert(budget, monthlyExpenses)
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Invalid budget amount", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error saving budget", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    true
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showNotificationPermissionDialog()
                    false
                }
                else -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                    false
                }
            }
        } else {
            true
        }
    }

    private fun showNotificationPermissionDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Notification Permission Required")
            .setMessage("Budget alerts require notification permission. Please enable it in app settings.")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent().apply {
            action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = android.net.Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    private fun startBudgetService() {
        try {
            val serviceIntent = Intent(requireContext(), BudgetNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(serviceIntent)
            } else {
                requireContext().startService(serviceIntent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "Could not start budget monitoring service",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateBudgetProgress() {
        val monthlyBudget = viewModel.budget.value ?: 0.0
        val monthlyExpenses = viewModel.getMonthlyExpenses()
        val progress = if (monthlyBudget > 0) {
            (monthlyExpenses / monthlyBudget * 100).toInt()
        } else 0
        binding.progressBudget.progress = progress
        binding.tvBudgetStatus.text = getString(R.string.budget_progress_with_currency, progress, currentCurrencyCode)
        // Check and notify for budget thresholds
        notificationHelper.showBudgetAlert(monthlyBudget, monthlyExpenses)

        // Show in-app notification using Toast
        val message = when {
            progress >= 100 -> "You've exceeded your budget!"
            progress >= 90 -> "Warning: Over 90% of your budget used!"
            progress >= 70 -> "Alert: Over 70% of your budget used!"
            else -> null
        }
        message?.let {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun setupBarChart() {
        try {
            binding.barChart.apply {
                description.isEnabled = false
                legend.isEnabled = false
                setTouchEnabled(true)
                setPinchZoom(true)
                axisLeft.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return formatCurrency(value.toDouble())
                    }
                }
                axisRight.isEnabled = false
                setNoDataText("No budget data available")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateBarChart() {
        try {
            val monthlyBudget = viewModel.budget.value ?: 0.0
            val monthlyExpenses = 0.0 // TODO: Implement monthly expenses from Room if needed
            val remaining = monthlyBudget - monthlyExpenses

            val entries = listOf(
                BarEntry(0f, monthlyExpenses.toFloat()),
                BarEntry(1f, remaining.toFloat())
            )

            val dataSet = BarDataSet(entries, "Budget").apply {
                colors = listOf(
                    ContextCompat.getColor(requireContext(), R.color.red_500),
                    ContextCompat.getColor(requireContext(), R.color.green_500)
                )
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return formatCurrency(value.toDouble())
                    }
                }
            }

            binding.barChart.data = BarData(dataSet)
            binding.barChart.invalidate()
        } catch (e: Exception) {
            e.printStackTrace()
            binding.barChart.setNoDataText("Error loading budget data")
            binding.barChart.invalidate()
        }
    }

    private fun formatCurrency(amount: Double): String {
        return try {
            com.example.spendmasterr.util.CurrencyFormatter.formatAmount(amount, currentCurrencyCode)
        } catch (e: Exception) {
            e.printStackTrace()
            "$${String.format("%.2f", amount)}"
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            NOTIFICATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startBudgetService()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Notification permission denied. You won't receive budget alerts.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
} 