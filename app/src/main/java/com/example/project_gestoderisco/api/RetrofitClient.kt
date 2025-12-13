package com.example.project_gestoderisco.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Base URL do backend (exemplo) – substitua pela sua API real
    private const val BASE_URL = "https://seuservidor.com/api/"

    // Instância única
    val apiService: OcorrenciaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OcorrenciaApiService::class.java)
    }
}