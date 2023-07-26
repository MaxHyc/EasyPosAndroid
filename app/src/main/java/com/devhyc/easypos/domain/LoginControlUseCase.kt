package com.devhyc.easypos.domain

import com.devhyc.easymanagementmobile.data.model.DTUserControlLogin
import com.devhyc.easypos.data.Repository
import com.devhyc.easypos.data.model.DTLogin
import com.devhyc.easypos.data.model.DTLoginRequest
import com.devhyc.easypos.data.model.Resultado
import javax.inject.Inject

class LoginControlUseCase @Inject constructor(private val repository: Repository) {
    suspend operator fun invoke(userlogin: DTLoginRequest): Resultado<DTUserControlLogin>? = repository.loginControl(userlogin)
}