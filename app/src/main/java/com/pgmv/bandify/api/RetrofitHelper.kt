package com.pgmv.bandify.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class RetrofitHelper private constructor(){
    companion object {
        private const val MUSIC_BASE_URL = "https://api.deezer.com/"
        private const val HOLIDAYS_BASE_URL = "https://brasilapi.com.br/api/"

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


    private val retrofitMusic: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(MUSIC_BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    private val retrofitHolidays: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(HOLIDAYS_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    fun <T> musicApi(serviceClass:Class<T>): T {
        return retrofitMusic.create(serviceClass)
    }

    fun <T> holidaysApi(serviceClass:Class<T>): T {
        return retrofitHolidays.create(serviceClass)
    }
}