package com.pgmv.bandify.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitHelper private constructor(){
    companion object {
        private const val BASE_URL = "https://api.deezer.com/"

        @Volatile
        private var instance: RetrofitHelper? = null

        fun getInstance(): RetrofitHelper {
            return instance ?: synchronized(this) {
                instance ?: RetrofitHelper().also { instance = it }
            }
        }
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "BandifyApp/1.0 (emfs6@aluno.ifal.edu.br)")
                .build()
            chain.proceed(request)
        }
        .build()


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    fun <T> musicApi(serviceClass:Class<T>): T {
        return retrofit.create(serviceClass)
    }
}