package com.example.project_gestoderisco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.repository.RiskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RiskViewModel(private val repository: RiskRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<RiskUiState>(RiskUiState.Loading)
    val uiState: StateFlow<RiskUiState> = _uiState

    init {
        fetchRisks()
    }

    fun fetchRisks() {
        viewModelScope.launch {
            _uiState.value = RiskUiState.Loading
            repository.getRisks()
                .catch { e -> _uiState.value = RiskUiState.Error(e.message ?: "Ocorreu um erro desconhecido") }
                .collect { risks -> _uiState.value = RiskUiState.Success(risks) }
        }
    }

    fun deleteRisk(riskId: String) {
        viewModelScope.launch {
            try {
                repository.deleteRisk(riskId)
                // A lista será atualizada automaticamente pelo listener em tempo real.
            } catch (e: Exception) {
                // Opcional: Emitir um estado de erro específico para a exclusão.
                _uiState.value = RiskUiState.Error("Falha ao excluir o risco: ${e.message}")
            }
        }
    }
}

class RiskViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RiskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RiskViewModel(RiskRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}