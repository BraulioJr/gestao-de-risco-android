package com.example.project_gestoderisco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.model.Risk
import com.example.project_gestoderisco.repository.RiskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

enum class SortOrder {
    BY_DATE_DESC, BY_IMPACT_DESC, BY_IMPACT_ASC, BY_PROBABILITY_DESC, BY_PROBABILITY_ASC
}

class RiskViewModel(private val repository: RiskRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<RiskUiState>(RiskUiState.Loading)
    val uiState: StateFlow<RiskUiState> = _uiState

    private val _sortOrder = MutableStateFlow(SortOrder.BY_DATE_DESC)
    val sortOrder: StateFlow<SortOrder> = _sortOrder.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        fetchRisks()
    }

    fun fetchRisks(newSortOrder: SortOrder = _sortOrder.value, newSearchQuery: String = _searchQuery.value) {
        _sortOrder.value = newSortOrder
        _searchQuery.value = newSearchQuery

        viewModelScope.launch {
            _uiState.value = RiskUiState.Loading
            repository.getRisks(newSortOrder, newSearchQuery)
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

    fun createRisk(risk: Risk) {
        viewModelScope.launch {
            try {
                repository.createRisk(risk)
                // Sucesso! A lista será atualizada automaticamente pelo listener em tempo real.
                // Se necessário, poderíamos emitir um estado de sucesso específico aqui.
            } catch (e: Exception) {
                _uiState.value = RiskUiState.Error("Falha ao criar o risco: ${e.message}")
            }
        }
    }

    fun updateRisk(risk: Risk) {
        viewModelScope.launch {
            try {
                repository.updateRisk(risk)
                // Sucesso! A lista será atualizada automaticamente pelo listener em tempo real.
            } catch (e: Exception) {
                _uiState.value = RiskUiState.Error("Falha ao atualizar o risco: ${e.message}")
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