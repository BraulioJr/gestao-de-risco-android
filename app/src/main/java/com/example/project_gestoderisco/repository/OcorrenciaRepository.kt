package com.example.project_gestoderisco.repository

import com.example.project_gestoderisco.api.RetrofitClient
import com.example.project_gestoderisco.data.local.OcorrenciaDao
import com.example.project_gestoderisco.data.local.OcorrenciaEntity
import com.example.project_gestoderisco.model.OcorrenciaRequest
import com.example.project_gestoderisco.model.OcorrenciaResponse

class OcorrenciaRepository(private val ocorrenciaDao: OcorrenciaDao) {

    private val api = RetrofitClient.apiService

    suspend fun salvarOcorrenciaLocal(ocorrencia: OcorrenciaEntity) {
        ocorrenciaDao.insert(ocorrencia)
    }

    // As funções de rede e sincronização virão aqui depois
    suspend fun registrarOcorrenciaApi(request: OcorrenciaRequest): OcorrenciaResponse {
        return api.registrarOcorrencia(request)
    }

    suspend fun getUnsyncedOcorrencias(): List<OcorrenciaEntity> {
        return ocorrenciaDao.getUnsyncedOcorrencias()
    }

    suspend fun markAsSynced(ids: List<String>) {
        ocorrenciaDao.markAsSynced(ids)
    }
}