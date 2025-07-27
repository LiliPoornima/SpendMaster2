package com.example.spendmasterr.ui

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spendmasterr.R
import com.example.spendmasterr.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson

class ProfileFragment : Fragment() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvAge: TextView
    private lateinit var tvGender: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvAccountType: TextView
    private lateinit var tvAccountNumber: TextView
    private lateinit var btnEditProfile: Button
    private lateinit var btnTopLogout: ImageButton
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvAge = view.findViewById(R.id.tvAge)
        tvGender = view.findViewById(R.id.tvGender)
        tvAddress = view.findViewById(R.id.tvAddress)
        tvAccountType = view.findViewById(R.id.tvAccountType)
        tvAccountNumber = view.findViewById(R.id.tvAccountNumber)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnTopLogout = view.findViewById(R.id.btnTopLogout)

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", android.content.Context.MODE_PRIVATE)

        // Load and display user data
        loadUserData()

        // Set click listeners
        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        btnTopLogout.setOnClickListener {
            // Clear user data and navigate to SignInActivity
            sharedPreferences.edit().clear().apply()
            val intent = Intent(activity, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            activity?.finish()
        }
    }

    private fun loadUserData() {
        val userJson = sharedPreferences.getString("user", null)
        if (userJson != null) {
            val gson = Gson()
            val user = gson.fromJson(userJson, User::class.java)
            tvUserName.text = user.name
            tvUserEmail.text = user.email
            tvAge.text = user.age.toString()
            tvGender.text = user.gender
            tvAddress.text = user.address
            tvAccountType.text = user.accountType
            tvAccountNumber.text = user.accountNumber
        }
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        val etName = dialogView.findViewById<TextInputEditText>(R.id.etName)
        val etAge = dialogView.findViewById<TextInputEditText>(R.id.etAge)
        val etGender = dialogView.findViewById<TextInputEditText>(R.id.etGender)
        val etAddress = dialogView.findViewById<TextInputEditText>(R.id.etAddress)
        val etAccountType = dialogView.findViewById<TextInputEditText>(R.id.etAccountType)
        val etAccountNumber = dialogView.findViewById<TextInputEditText>(R.id.etAccountNumber)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        // Load current values
        val userJson = sharedPreferences.getString("user", null)
        if (userJson != null) {
            val gson = Gson()
            val user = gson.fromJson(userJson, User::class.java)
            etName.setText(user.name)
            etAge.setText(user.age.toString())
            etGender.setText(user.gender)
            etAddress.setText(user.address)
            etAccountType.setText(user.accountType)
            etAccountNumber.setText(user.accountNumber)
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val age = etAge.text.toString().toIntOrNull() ?: 25
            val gender = etGender.text.toString()
            val address = etAddress.text.toString()
            val accountType = etAccountType.text.toString()
            val accountNumber = etAccountNumber.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update user data
            val updatedUser = User(
                name = name,
                email = tvUserEmail.text.toString(),
                password = "", // Keep existing password
                age = age,
                gender = gender,
                address = address,
                accountType = accountType,
                accountNumber = accountNumber
            )

            val gson = Gson()
            val updatedUserJson = gson.toJson(updatedUser)
            sharedPreferences.edit().putString("user", updatedUserJson).apply()

            // Update UI
            loadUserData()
            dialog.dismiss()
            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }
} 