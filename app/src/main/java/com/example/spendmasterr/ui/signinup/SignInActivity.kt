package com.example.spendmasterr.ui.signinup

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.spendmasterr.MainActivity
import com.example.spendmasterr.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    // Temporary hardcoded credentials for testing
    // TODO: Replace with proper authentication system
    private val validEmail = "user@example.com"
    private val validPassword = "password123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set click listeners
        binding.loginlogbtn.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate credentials
            if (email == validEmail && password == validPassword) {
                // Successful login
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                // Failed login
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
                binding.editTextPassword.text.clear()
            }
        }

        binding.loginsignbtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Social media button click listeners
        binding.elehmbtn.setOnClickListener {
            // TODO: Implement Facebook login
            Toast.makeText(this, "Facebook login coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.clenhmbtn.setOnClickListener {
            // TODO: Implement Google login
            Toast.makeText(this, "Google login coming soon", Toast.LENGTH_SHORT).show()
        }

        binding.roofhmbtn.setOnClickListener {
            // TODO: Implement X/Twitter login
            Toast.makeText(this, "X/Twitter login coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}