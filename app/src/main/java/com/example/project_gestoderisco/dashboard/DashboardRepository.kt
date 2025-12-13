package com.example.project_gestoderisco.dashboard

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class DashboardRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // Busca ocorrências entre start (inclusive) e end (inclusive), opcional filtro por lojaId
    suspend fun fetchOcorrencias(
        startEpochMs: Long,
        endEpochMs: Long,
        lojaId: Int? = null
    ): List<Map<String, Any>> {
        var query: Query = firestore.collection("ocorrencias")
            .whereGreaterThanOrEqualTo("dataHora", startEpochMs)
            .whereLessThanOrEqualTo("dataHora", endEpochMs)

        if (lojaId != null) {
            query = query.whereEqualTo("lojaId", lojaId)
        }

        val snap = query.get().await()
        return snap.documents.mapNotNull { it.data }
    }
}