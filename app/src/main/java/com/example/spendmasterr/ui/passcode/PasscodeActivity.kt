package com.example.spendmasterr.ui.passcode

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendmasterr.MainActivity
import com.example.spendmasterr.databinding.ActivityPasscodeBinding

class PasscodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPasscodeBinding
    private val correctPasscode = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasscodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSubmit.setOnClickListener {
            val enteredPasscode = binding.editPasscode.text.toString()
            if (enteredPasscode == correctPasscode) {
                navigateToMainActivity()
            } else {
                Toast.makeText(this, "Incorrect passcode. Please try again.", Toast.LENGTH_SHORT).show()
                binding.editPasscode.text?.clear()
            }
        }
    }

    private fun navigateToMainActivity() {
        try {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error navigating to main screen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
} 