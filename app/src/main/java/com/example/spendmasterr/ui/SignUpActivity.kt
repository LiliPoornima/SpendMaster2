package com.example.spendmasterr.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.spendmasterr.databinding.ActivitySignUpBinding
import com.example.spendmasterr.ui.SignInActivity
import android.util.Patterns

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize encrypted shared preferences
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        sharedPreferences = EncryptedSharedPreferences.create(
            "user_credentials",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.signsignupbtn.setOnClickListener {
            validateAndSignUp()
        }

        // Social media sign-up buttons
        binding.elehmbtn.setOnClickListener {
            // Facebook sign up implementation
            Toast.makeText(this, "Facebook sign up clicked", Toast.LENGTH_SHORT).show()
        }

        binding.clenhmbtn.setOnClickListener {
            // Google sign up implementation
            Toast.makeText(this, "Google sign up clicked", Toast.LENGTH_SHORT).show()
        }

        binding.roofhmbtn.setOnClickListener {
            // X/Twitter sign up implementation
            Toast.makeText(this, "X sign up clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateAndSignUp() {
        val name = binding.nameInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val password = binding.passwordInput.text.toString()
        val confirmPassword = binding.confirmPasswordInput.text.toString()

        if (name.isEmpty()) {
            binding.nameInput.error = "Name is required"
            return
        }

        if (email.isEmpty()) {
            binding.emailInput.error = "Email is required"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.error = "Please enter a valid email"
            return
        }

        if (password.isEmpty()) {
            binding.passwordInput.error = "Password is required"
            return
        }

        if (password.length < 6) {
            binding.passwordInput.error = "Password must be at least 6 characters"
            return
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordInput.error = "Please confirm your password"
            return
        }

        if (password != confirmPassword) {
            binding.confirmPasswordInput.error = "Passwords do not match"
            return
        }

        // Save credentials
        try {
            with(sharedPreferences.edit()) {
                putString("name", name)
                putString("email", email)
                putString("password", password)
                apply()
            }
            
            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving credentials: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
} 