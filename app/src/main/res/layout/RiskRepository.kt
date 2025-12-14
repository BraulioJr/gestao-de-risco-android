package com.example.project_gestoderisco.repository

import com.example.project_gestoderisco.model.Risk
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class RiskRepository {

    private val firestore = Firebase.firestore
    private val auth = Firebase.auth

    /**
     * Ouve as atualizações da lista de riscos do usuário logado no Firestore em tempo real.
     * Retorna um Flow que emite a lista de riscos sempre que houver uma alteração.
     */
    fun getRisks(): Flow<List<Risk>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listenerRegistration = firestore.collection("users").document(userId).collection("risks")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Fecha o flow com erro
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val risks = snapshot.toObjects<Risk>()
                    trySend(risks) // Emite a nova lista para o flow
                }
            }

        // Garante que o listener seja removido quando o Flow for cancelado
        awaitClose { listenerRegistration.remove() }
    }
}