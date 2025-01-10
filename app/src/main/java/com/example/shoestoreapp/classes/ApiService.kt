package com.example.shoestoreapp.classes

import com.example.shoestoreapp.data.model.CityResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api-tinhthanh/{A}/{B}.htm")
    fun getCityData(@Path("A") a: Int, @Path("B") b: Int): Call<CityResponse>
}