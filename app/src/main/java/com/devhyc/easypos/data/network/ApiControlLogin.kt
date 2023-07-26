package com.devhyc.easypos.data.network

import com.devhyc.easymanagementmobile.data.model.DTUserControlLogin
import com.devhyc.easypos.data.model.DTLoginRequest
import com.devhyc.easypos.data.model.Resultado
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiControlLogin {
    @POST("usuariosapp/login")
    suspend fun loginusuariosconfig(@Body login: DTLoginRequest): Response<Resultado<DTUserControlLogin>>
}