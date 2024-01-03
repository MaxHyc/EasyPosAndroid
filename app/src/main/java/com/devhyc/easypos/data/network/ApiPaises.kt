package com.devhyc.easypos.data.network

import com.devhyc.easypos.data.model.Squareup.Country
import retrofit2.http.GET

interface ApiPaises {
    @GET("all")
    suspend fun getAllContries(): List<Country>
}