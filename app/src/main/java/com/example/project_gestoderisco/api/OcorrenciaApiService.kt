package com.example.project_gestoderisco.api

import com.example.project_gestoderisco.model.OcorrenciaRequest
import com.example.project_gestoderisco.model.OcorrenciaResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OcorrenciaApiService {

    // Define a requisição POST para o endpoint "/ocorrencias"
    // O suspend keyword indica que esta é uma função de coroutine e deve ser chamada de forma assíncrona
    @POST("ocorrencias")
    suspend fun registrarOcorrencia(@Body request: OcorrenciaRequest): OcorrenciaResponse
}