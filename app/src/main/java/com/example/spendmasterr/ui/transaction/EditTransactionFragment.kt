package com.example.spendmasterr.ui.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.spendmasterr.data.Result
import com.example.spendmasterr.model.Transaction
import com.example.spendmasterr.model.TransactionType
import com.example.spendmasterr.databinding.FragmentEditTransactionBinding
import android.widget.ArrayAdapter

class EditTransactionFragment : Fragment() {
    private var _binding: FragmentEditTransactionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EditTransactionViewModel
    private val args: EditTransactionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val categoriesProvider = {
            listOf(
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
        }
        viewModel = ViewModelProvider(this, EditTransactionViewModelFactory(requireContext(), categoriesProvider))
            .get(EditTransactionViewModel::class.java)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        val transaction = args.transaction
        binding.apply {
            // Set up category spinner
            val categories = viewModel.getCategories()
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
            
            // Set initial values
            editTextTitle.setText(transaction.description)
            editTextAmount.setText(transaction.amount.toString())
            
            // Set category selection
            val categoryPosition = categories.indexOf(transaction.category)
            if (categoryPosition != -1) {
                spinnerCategory.setSelection(categoryPosition)
            }
            
            // Set transaction type
            if (transaction.type == TransactionType.INCOME) {
                radioIncome.isChecked = true
            } else {
                radioExpense.isChecked = true
            }

            // Set up click listeners
            buttonSave.setOnClickListener {
                updateTransaction(transaction)
            }

            buttonCancel.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun updateTransaction(originalTransaction: Transaction) {
        binding.apply {
            val description = editTextTitle.text.toString()
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val category = spinnerCategory.selectedItem?.toString() ?: ""
            val type = if (radioIncome.isChecked) TransactionType.INCOME else TransactionType.EXPENSE

            if (description.isBlank() || amount == null || category.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return
            }

            val updatedTransaction = Transaction(
                id = originalTransaction.id,
                amount = amount,
                description = description,
                category = category,
                type = type,
                date = originalTransaction.date,
                isRecurring = originalTransaction.isRecurring,
                recurringPeriod = originalTransaction.recurringPeriod
            )

            viewModel.updateTransaction(updatedTransaction)
        }
    }

    private fun observeViewModel() {
        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    Toast.makeText(requireContext(), "Transaction updated", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), result.exception.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 