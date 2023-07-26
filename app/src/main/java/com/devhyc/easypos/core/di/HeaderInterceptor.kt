package com.devhyc.easypos.core.di

import com.devhyc.easypos.utilidades.Globales
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url().toString().replace("https://hyc.uy/",Globales.DireccionServidor)
        var request = chain.request().newBuilder()
        request.url(url)
        if (Globales.UsuarioLoggueado != null)
        {
            request.addHeader("Authorization", "Bearer ${Globales.UsuarioLoggueado.token}")
        }
        return chain.proceed(request.build())
    }
}