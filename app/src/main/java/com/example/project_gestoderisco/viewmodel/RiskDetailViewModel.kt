package com.example.project_gestoderisco.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.model.Risk
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed interface RiskDetailUiState {
    data object Loading : RiskDetailUiState
    data class Success(val risk: Risk) : RiskDetailUiState
    data class Error(val message: String) : RiskDetailUiState
}

sealed interface SaveResultUiState {
    data object Idle : SaveResultUiState
    data object Loading : SaveResultUiState
    data object Success : SaveResultUiState
    data class Error(val message: String) : SaveResultUiState
}

sealed interface DeleteResultUiState {
    data object Idle : DeleteResultUiState
    data object Loading : DeleteResultUiState
    data object Success : DeleteResultUiState
    data class Error(val message: String) : DeleteResultUiState
}

class RiskDetailViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    private val _riskState = MutableStateFlow<RiskDetailUiState>(RiskDetailUiState.Loading)
    val riskState: StateFlow<RiskDetailUiState> = _riskState

    private val _saveState = MutableStateFlow<SaveResultUiState>(SaveResultUiState.Idle)
    val saveState: StateFlow<SaveResultUiState> = _saveState

    private val _deleteState = MutableStateFlow<DeleteResultUiState>(DeleteResultUiState.Idle)
    val deleteState: StateFlow<DeleteResultUiState> = _deleteState

    fun loadRisk(riskId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("risks").document(riskId).get().await()
                val risk = snapshot.toObject<Risk>()
                if (risk != null) {
                    _riskState.value = RiskDetailUiState.Success(risk)
                } else {
                    _riskState.value = RiskDetailUiState.Error("Risco não encontrado.")
                }
            } catch (e: Exception) {
                _riskState.value = RiskDetailUiState.Error(e.message ?: "Erro ao carregar o risco.")
            }
        }
    }

    fun saveRisk(
        id: String?,
        name: String,
        description: String,
        impact: Long,
        probability: Long,
        attachmentUri: Uri?,
        currentAttachmentUrl: String?
    ) {
        viewModelScope.launch {
            _saveState.value = SaveResultUiState.Loading
            val userId = auth.currentUser?.uid ?: run {
                _saveState.value = SaveResultUiState.Error("Usuário não autenticado.")
                return@launch
            }
            
            val attachmentUrl = if (attachmentUri != null) {
                uploadAttachment(attachmentUri)
            } else {
                currentAttachmentUrl
            }
            
            val risk = Risk(id ?: "", name, description, impact, probability, ownerId = userId, attachmentUrl = attachmentUrl)
            
            try {
                val document = if (id.isNullOrEmpty()) db.collection("risks").document() else db.collection("risks").document(id)
                document.set(risk.copy(id = document.id)).await()
                _saveState.value = SaveResultUiState.Success
            } catch (e: Exception) {
                _saveState.value = SaveResultUiState.Error(e.message ?: "Erro ao salvar o risco.")
            }
        }
    }

    fun deleteRisk(riskId: String) {
        viewModelScope.launch {
            _deleteState.value = DeleteResultUiState.Loading
            try {
                db.collection("risks").document(riskId).delete().await()
                _deleteState.value = DeleteResultUiState.Success
            } catch (e: Exception) {
                _deleteState.value = DeleteResultUiState.Error(e.message ?: "Erro ao excluir o risco.")
            }
        }
    }

    private suspend fun uploadAttachment(uri: Uri): String? {
        return try {
            val storageRef = storage.reference.child("attachments/${System.currentTimeMillis()}_${uri.lastPathSegment}")
            storageRef.putFile(uri).await()
            storageRef.downloadUrl.await()?.toString()
        } catch (e: Exception) { null }
    }
}