package com.devhyc.easypos.core.di

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptorControl: Interceptor
{
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request().newBuilder()
        request.addHeader("Authorization","Bearer hyccontrolapps")
        return chain.proceed(request.build())
    }
}