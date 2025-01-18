package com.example.shoestoreapp.data.model

data class Address (
    val id: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val city: String = "",
    val houseNo: String = "",
    var default: Boolean = false
) : java.io.Serializable