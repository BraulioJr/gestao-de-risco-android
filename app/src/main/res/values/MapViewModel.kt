package com.example.gestaoderisco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestaoderisco.models.Loja
import com.example.gestaoderisco.models.Ocorrencia
import com.example.gestaoderisco.repository.OcorrenciasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MapUiState(
    val lojas: List<Loja> = emptyList(),
    val ocorrencias: List<Ocorrencia> = emptyList(),
    val isLoading: Boolean = true
)

class MapViewModel : ViewModel() {
    private val repository = OcorrenciasRepository()

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Busca as ocorrências do banco local
            repository.getOcorrencias("").collect { ocorrencias ->
                // Para cada atualização na lista de ocorrências, busca as lojas (poderia ser otimizado)
                val lojas = repository.getLojas()
                _uiState.value = MapUiState(lojas = lojas, ocorrencias = ocorrencias, isLoading = false)
            }
        }
    }
}