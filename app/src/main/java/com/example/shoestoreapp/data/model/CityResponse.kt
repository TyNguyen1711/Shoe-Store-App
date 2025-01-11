package com.example.shoestoreapp.data.model

data class CityResponse(
    val error: Int,
    val error_text: String,
    val data_name: String,
    val data: List<City>
)

data class City(
    val id: String,
    val name: String,
    val full_name: String,
    val data2: List<District>
)

data class District(
    val id: String,
    val name: String,
    val full_name: String,
    val data3: List<Ward>
)

data class Ward(
    val id: String,
    val name: String,
    val full_name: String
)