package com.example.spendmasterr.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spendmasterr.R
import com.example.spendmasterr.model.Transaction
import com.example.spendmasterr.model.TransactionType
import com.example.spendmasterr.databinding.FragmentDashboardBinding
import com.example.spendmasterr.util.CurrencyFormatter
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import com.example.spendmasterr.viewmodel.SharedCurrencyViewModel

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    private lateinit var sharedCurrencyViewModel: SharedCurrencyViewModel
    private var currentCurrencyCode: String = "USD"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupUI()
        observeViewModel()
        sharedCurrencyViewModel = ViewModelProvider(requireActivity()).get(SharedCurrencyViewModel::class.java)
        sharedCurrencyViewModel.currencyCode.observe(viewLifecycleOwner) { code ->
            currentCurrencyCode = code
            // Update all currency-related UI
            updateAllCurrencyUI()
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return DashboardViewModel(requireContext()) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        })[DashboardViewModel::class.java]
    }

    private fun setupUI() {
        setupPieChart()
        setupLineCharts()
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setHoleColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
            setTransparentCircleColor(
                ContextCompat.getColor(
                    requireContext(),
                    android.R.color.transparent
                )
            )
            setEntryLabelColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            setEntryLabelTextSize(12f)
            setUsePercentValues(true)
            setDrawEntryLabels(true)
            setDrawHoleEnabled(true)
            setHoleRadius(50f)
            setTransparentCircleRadius(55f)
            setRotationEnabled(true)
            setHighlightPerTapEnabled(true)
            animateY(1000)
            setNoDataText("No transactions yet")
        }
    }

    private fun setupLineCharts() {
        val commonSetup: com.github.mikephil.charting.charts.LineChart.() -> Unit = {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return try {
                        dateFormat.format(Date(value.toLong()))
                    } catch (e: Exception) {
                        ""
                    }
                }
            }
            axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return formatCurrency(value.toDouble())
                }
            }
            axisRight.isEnabled = false
            setNoDataText("No data available")
        }

        binding.incomeChart.apply(commonSetup)
        binding.expenseChart.apply(commonSetup)
    }

    private fun observeViewModel() {
        viewModel.totalBalance.observe(viewLifecycleOwner) { balance ->
            binding.tvTotalBalance.text = formatCurrency(balance ?: 0.0)
        }

        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvTotalIncome.text = formatCurrency(income ?: 0.0)
        }

        viewModel.totalExpense.observe(viewLifecycleOwner) { expense ->
            binding.tvTotalExpense.text = formatCurrency(expense ?: 0.0)
        }

        viewModel.categorySpending.observe(viewLifecycleOwner) { spending ->
            updatePieChart(spending ?: emptyMap())
        }

        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            updateLineCharts(transactions ?: emptyList())
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboardData()
        binding.apply {
            tvTotalBalance.text = formatCurrency(viewModel.totalBalance.value ?: 0.0)
            tvTotalIncome.text = formatCurrency(viewModel.totalIncome.value ?: 0.0)
            tvTotalExpense.text = formatCurrency(viewModel.totalExpense.value ?: 0.0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun formatCurrency(amount: Double): String {
        return try {
            CurrencyFormatter.formatAmount(amount, currentCurrencyCode)
        } catch (e: Exception) {
            e.printStackTrace()
            "$${String.format("%.2f", amount)}"
        }
    }

    private fun updatePieChart(spending: Map<String, Double>) {
        if (spending.isEmpty()) {
            binding.pieChart.setNoDataText("No transactions yet")
            binding.pieChart.invalidate()
            return
        }

        val entries = spending.map { (category, amount) ->
            PieEntry(amount.toFloat(), category)
        }

        val dataSet = PieDataSet(entries, "Categories")
        dataSet.colors = getChartColors()
        
        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(11f)
        
        binding.pieChart.data = pieData
        binding.pieChart.invalidate()
    }

    private fun updateLineCharts(transactions: List<Transaction>) {
        if (transactions.isEmpty()) {
            binding.incomeChart.setNoDataText("No income data available")
            binding.expenseChart.setNoDataText("No expense data available")
            binding.incomeChart.invalidate()
            binding.expenseChart.invalidate()
            return
        }

        // Group transactions by type and date
        val incomeData = transactions
            .filter { it.type == TransactionType.INCOME }
            .groupBy { it.date.time }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }
            .toSortedMap()

        val expenseData = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.date.time }
            .mapValues { it.value.sumOf { transaction -> transaction.amount } }
            .toSortedMap()

        // Create income entries
        val incomeEntries = incomeData.map {
            Entry(it.key.toFloat(), it.value.toFloat())
        }

        // Create expense entries
        val expenseEntries = expenseData.map {
            Entry(it.key.toFloat(), it.value.toFloat())
        }

        // Update income chart
        if (incomeEntries.isNotEmpty()) {
            val incomeDataSet = LineDataSet(incomeEntries, "Income").apply {
                color = ContextCompat.getColor(requireContext(), R.color.income_color)
                setDrawCircles(true)
                setDrawValues(false)
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(ContextCompat.getColor(requireContext(), R.color.income_color))
                mode = LineDataSet.Mode.LINEAR
                cubicIntensity = 0.2f
            }

            binding.incomeChart.data = LineData(incomeDataSet)
            binding.incomeChart.invalidate()
        }

        // Update expense chart
        if (expenseEntries.isNotEmpty()) {
            val expenseDataSet = LineDataSet(expenseEntries, "Expenses").apply {
                color = ContextCompat.getColor(requireContext(), R.color.expense_color)
                setDrawCircles(true)
                setDrawValues(false)
                lineWidth = 2f
                circleRadius = 4f
                setCircleColor(ContextCompat.getColor(requireContext(), R.color.expense_color))
                mode = LineDataSet.Mode.LINEAR
                cubicIntensity = 0.2f
            }

            binding.expenseChart.data = LineData(expenseDataSet)
            binding.expenseChart.invalidate()
        }
    }

    private fun getChartColors(): List<Int> {
        return listOf(
            R.color.chart_color_1,
            R.color.chart_color_2,
            R.color.chart_color_3,
            R.color.chart_color_4,
            R.color.chart_color_5
        ).map { ContextCompat.getColor(requireContext(), it) }
    }

    private fun updateAllCurrencyUI() {
        binding.tvTotalBalance.text = formatCurrency(viewModel.totalBalance.value ?: 0.0)
        binding.tvTotalIncome.text = formatCurrency(viewModel.totalIncome.value ?: 0.0)
        binding.tvTotalExpense.text = formatCurrency(viewModel.totalExpense.value ?: 0.0)
        // Add more UI updates if needed
    }
} 