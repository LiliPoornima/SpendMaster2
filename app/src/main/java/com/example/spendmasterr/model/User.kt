package com.example.spendmasterr.model

data class User(
    val name: String,
    val email: String,
    val password: String,
    val age: Int = 25,
    val address: String = "102/2, Pilimathalawa, Kandy",
    val gender: String = "Female",
    val accountType: String = "Current",
    val accountNumber: String = "14562001755784"
) 