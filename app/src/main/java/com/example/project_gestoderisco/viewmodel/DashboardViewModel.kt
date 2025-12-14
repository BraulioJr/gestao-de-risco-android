package com.example.project_gestoderisco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.model.Risk
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardStats(
    val totalRisks: Int = 0,
    val highImpactCount: Int = 0,
    val mediumImpactCount: Int = 0,
    val lowImpactCount: Int = 0
)

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val stats: DashboardStats) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        val userId = auth.currentUser?.uid ?: run {
            _uiState.value = DashboardUiState.Error("Usuário não autenticado.")
            return
        }

        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            db.collection("risks")
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val risks = snapshot.toObjects<Risk>()
                    val stats = DashboardStats(
                        totalRisks = risks.size,
                        highImpactCount = risks.count { it.impact >= 7 },
                        mediumImpactCount = risks.count { it.impact in 4..6 },
                        lowImpactCount = risks.count { it.impact < 4 }
                    )
                    _uiState.value = DashboardUiState.Success(stats)
                }
                .addOnFailureListener { e ->
                    _uiState.value = DashboardUiState.Error(e.message ?: "Erro ao buscar dados do dashboard.")
                }
        }
    }
}