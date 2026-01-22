package com.example.gestaoderisco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestaoderisco.repository.OcorrenciasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class OcorrenciasViewModel(private val repository: OcorrenciasRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<OcorrenciasUiState>(OcorrenciasUiState.Loading)
    val uiState: StateFlow<OcorrenciasUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300) // Espera 300ms após o usuário parar de digitar
                .collect { query ->
                    fetchOcorrencias(query)
                }
        }
    }

    private fun fetchOcorrencias(searchText: String) {
        viewModelScope.launch {
            _uiState.value = OcorrenciasUiState.Loading
            repository.getOcorrencias(searchText)
                .catch { e -> _uiState.value = OcorrenciasUiState.Error(e.message ?: "Erro desconhecido") }
                .collect { ocorrencias -> _uiState.value = OcorrenciasUiState.Success(ocorrencias) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}

class OcorrenciasViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OcorrenciasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OcorrenciasViewModel(OcorrenciasRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}