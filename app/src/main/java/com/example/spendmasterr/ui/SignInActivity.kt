package com.example.spendmasterr.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.spendmasterr.MainActivity
import com.example.spendmasterr.databinding.ActivitySignInBinding
import android.util.Patterns

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
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

        // Set click listeners
        binding.loginlogbtn.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editTextEmail.error = "Please enter a valid email"
                return@setOnClickListener
            }

            // Get saved credentials
            val savedEmail = sharedPreferences.getString("email", "")
            val savedPassword = sharedPreferences.getString("password", "")
            val savedName = sharedPreferences.getString("name", "")

            // Validate credentials
            if (email == savedEmail && password == savedPassword) {
                // Successful login
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("user_name", savedName)
                startActivity(intent)
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