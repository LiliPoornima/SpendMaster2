package com.example.spendmasterr.ui.settings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.spendmasterr.R
import com.example.spendmasterr.databinding.FragmentSettingsBinding
import com.example.spendmasterr.data.database.SpendMasterDatabase
import com.example.spendmasterr.data.repository.CurrencyRepository
import com.example.spendmasterr.util.ExportManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.fragment.findNavController
import com.example.spendmasterr.viewmodel.SharedCurrencyViewModel
import com.example.spendmasterr.util.NotificationHelper
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build.VERSION_CODES
import android.os.Build.VERSION
import android.content.Context
import androidx.core.app.NotificationCompat

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SettingsViewModel
    private lateinit var exportManager: ExportManager
    private lateinit var sharedCurrencyViewModel: SharedCurrencyViewModel
    private lateinit var notificationHelper: NotificationHelper

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            performExport()
        } else {
            Toast.makeText(
                requireContext(),
                "Storage permission is required to export data",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val db = SpendMasterDatabase.getDatabase(requireContext())
        val repository = CurrencyRepository(db.currencyDao())
        viewModel = ViewModelProvider(this, SettingsViewModelFactory(repository))
            .get(SettingsViewModel::class.java)
        exportManager = ExportManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedCurrencyViewModel = ViewModelProvider(requireActivity()).get(SharedCurrencyViewModel::class.java)
        setupUI()
        setupClickListeners()
        observeViewModel()
        
        // Set initial dark mode state
        binding.switchDarkMode.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        notificationHelper = NotificationHelper(requireContext())
    }

    private fun setupUI() {
        // Setup currency spinner
        val currencies = listOf(
            getString(R.string.currency_usd),
            getString(R.string.currency_eur),
            getString(R.string.currency_gbp),
            getString(R.string.currency_jpy),
            getString(R.string.currency_inr),
            getString(R.string.currency_aud),
            getString(R.string.currency_cad),
            getString(R.string.currency_lkr),
            getString(R.string.currency_cny),
            getString(R.string.currency_sgd),
            getString(R.string.currency_myr),
            getString(R.string.currency_thb),
            getString(R.string.currency_idr),
            getString(R.string.currency_php),
            getString(R.string.currency_vnd),
            getString(R.string.currency_krw),
            getString(R.string.currency_aed),
            getString(R.string.currency_sar),
            getString(R.string.currency_qar)
        )
        
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            currencies
        )
        
        binding.spinnerCurrency.apply {
            setAdapter(adapter)
            threshold = 1
            viewModel.currency.observe(viewLifecycleOwner) { currentCurrency ->
            val currencyIndex = currencies.indexOfFirst { it.startsWith(currentCurrency) }
            if (currencyIndex != -1) {
                setText(currencies[currencyIndex], false)
            }
            }
            setOnItemClickListener { _, _, position, _ ->
                val selectedCurrency = adapter.getItem(position).toString()
                val currencyCode = selectedCurrency.substring(0, 3)
                viewModel.setSelectedCurrency(currencyCode)
                Toast.makeText(requireContext(), "Currency updated to $selectedCurrency", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSaveCurrency.setOnClickListener {
                val selectedCurrency = spinnerCurrency.text.toString()
                val currencyCode = selectedCurrency.substring(0, 3)
                sharedCurrencyViewModel.setCurrency(currencyCode)
                Toast.makeText(requireContext(), "Currency saved", Toast.LENGTH_SHORT).show()
            }

            switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
                val mode = if (isChecked) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(mode)
            }

            btnExportJson.setOnClickListener {
                checkStoragePermissionAndExport()
            }

            btnRestore.setOnClickListener {
                // You may want to prompt for a file name, or use the latest export file name logic
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                // Find the latest export file (optional, or use a fixed name)
                val exportFile = downloadsDir.listFiles()?.filter { it.name.startsWith("spendmaster_export_") && it.name.endsWith(".txt") }
                    ?.maxByOrNull { it.lastModified() }
                val fileName = exportFile?.name ?: "spendmaster_export.txt" // fallback

                Toast.makeText(requireContext(), "Restoring from: $fileName", Toast.LENGTH_SHORT).show()

                CoroutineScope(Dispatchers.Main).launch {
                    val result = withContext(Dispatchers.IO) {
                        exportManager.restoreFromTxt(fileName)
                    }
                    if (result.isSuccess) {
                        Toast.makeText(requireContext(), "Restore successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Restore failed for file: $fileName\n${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun checkStoragePermissionAndExport() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, we don't need storage permission for Downloads folder
            performExport()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    performExport()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    showStoragePermissionDialog()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun showStoragePermissionDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Storage Permission Required")
            .setMessage("Storage permission is required to export data to a file. Please grant the permission.")
            .setPositiveButton("Grant") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performExport() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    exportManager.exportToJson()
                }
                
                result.fold(
                    onSuccess = { filePath ->
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.export_success, filePath),
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onFailure = { exception ->
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.export_error, exception.message),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.export_error, e.message),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun observeViewModel() {
        // Implement the logic to observe the ViewModel
    }

    private fun checkAndNotifyBudgetUsage(monthlyBudget: Double, monthlyExpenses: Double) {
        val percentageUsed = if (monthlyBudget > 0) (monthlyExpenses / monthlyBudget * 100).toInt() else 0

        when {
            percentageUsed >= 100 -> showBudgetNotification("Budget Exceeded!", "You've exceeded your monthly budget by ${formatCurrency(monthlyExpenses - monthlyBudget)}")
            percentageUsed >= 90 -> showBudgetNotification("Budget Warning!", "You've used $percentageUsed% of your monthly budget. Only ${formatCurrency(monthlyBudget - monthlyExpenses)} remaining.")
            percentageUsed >= 70 -> showBudgetNotification("Budget Alert!", "You've used $percentageUsed% of your monthly budget. ${formatCurrency(monthlyBudget - monthlyExpenses)} remaining.")
        }
    }

    private fun showBudgetNotification(title: String, message: String) {
        notificationHelper.showBudgetNotification(title, message)
    }

    private fun formatCurrency(amount: Double, currencyCode: String = "USD"): String {
        return try {
            com.example.spendmasterr.util.CurrencyFormatter.formatAmount(amount, currencyCode)
        } catch (e: Exception) {
            "$${String.format("%.2f", amount)}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 