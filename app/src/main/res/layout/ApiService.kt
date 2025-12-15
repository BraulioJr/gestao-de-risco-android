package com.example.gestaoderisco.data.remote

import com.example.gestaoderisco.data.local.Ocorrencia
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("ocorrencias")
    suspend fun getOcorrencias(): Response<List<Ocorrencia>>
}