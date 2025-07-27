package com.example.spendmasterr.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.spendmasterr.data.Result
import com.example.spendmasterr.model.Transaction
import com.example.spendmasterr.model.TransactionType
import com.example.spendmasterr.databinding.FragmentAddTransactionBinding
import java.util.UUID
import java.util.Date
import com.example.spendmasterr.data.database.SpendMasterDatabase
import com.example.spendmasterr.data.repository.TransactionRepository

class AddTransactionFragment : Fragment() {
    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddTransactionViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val db = SpendMasterDatabase.getDatabase(requireContext())
        val repository = TransactionRepository(db.transactionDao())
        val factory = AddTransactionViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AddTransactionViewModel::class.java)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            // Set up category spinner
            val categories = listOf(
                "Salary",
                "Bonus",
                "Investment",
                "Food",
                "Transport",
                "Entertainment",
                "Bills",
                "Shopping",
                "Other"
            )
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter

            // Set default selection
            spinnerCategory.setSelection(0)

            // Set default radio button selection
            radioIncome.isChecked = true

            buttonSave.setOnClickListener {
                saveTransaction()
            }

            buttonCancel.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun saveTransaction() {
        binding.apply {
            val description = editTextTitle.text.toString()
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val category = spinnerCategory.selectedItem?.toString() ?: ""
            val type = if (radioIncome.isChecked) TransactionType.INCOME else TransactionType.EXPENSE

            if (description.isBlank()) {
                editTextTitle.error = "Description is required"
                return
            }
            if (amount == null) {
                editTextAmount.error = "Amount is required"
                return
            }
            if (category.isBlank()) {
                Toast.makeText(requireContext(), "Please select a category", Toast.LENGTH_SHORT).show()
                return
            }

            val transaction = Transaction(
                id = UUID.randomUUID().toString(),
                description = description,
                amount = amount,
                category = category,
                type = type,
                date = Date(),
                isRecurring = false,
                recurringPeriod = null
            )

            viewModel.addTransaction(transaction)
        }
    }

    private fun observeViewModel() {
        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(requireContext(), "Transaction saved successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), "Error saving transaction: ${result.exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 