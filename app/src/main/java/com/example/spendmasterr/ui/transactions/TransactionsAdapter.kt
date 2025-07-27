package com.example.spendmasterr.ui.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spendmasterr.R
import com.example.spendmasterr.model.Transaction
import com.example.spendmasterr.model.TransactionType
import java.text.SimpleDateFormat
import java.util.*

class TransactionsAdapter(
    private val onEditClick: (Transaction) -> Unit,
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionsAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val categoryTextView: TextView = itemView.findViewById(R.id.tvCategory)
        private val amountTextView: TextView = itemView.findViewById(R.id.tvAmount)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvDate)
        private val typeIndicator: View = itemView.findViewById(R.id.viewTypeIndicator)
        private val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(transaction: Transaction) {
            titleTextView.text = transaction.description
            categoryTextView.text = transaction.category
            amountTextView.text = formatAmount(transaction.amount, transaction.type)
            dateTextView.text = formatDate(transaction.date)
            
            // Set color based on transaction type
            val colorRes = if (transaction.type == TransactionType.INCOME) R.color.green_500 else R.color.red_500
            typeIndicator.setBackgroundColor(ContextCompat.getColor(itemView.context, colorRes))
            
            // Set amount text color
            amountTextView.setTextColor(ContextCompat.getColor(itemView.context, colorRes))
            
            editButton.setOnClickListener { onEditClick(transaction) }
            deleteButton.setOnClickListener { onDeleteClick(transaction) }
        }

        private fun formatAmount(amount: Double, type: TransactionType): String {
            val prefix = if (type == TransactionType.INCOME) "+" else "-"
            return "$prefix$${String.format("%.2f", amount)}"
        }

        private fun formatDate(date: Date): String {
            return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
        }
    }

    private class TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
} 