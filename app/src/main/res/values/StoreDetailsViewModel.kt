package com.example.gestaoderisco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestaoderisco.models.Ocorrencia
import com.example.gestaoderisco.repository.OcorrenciasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StoreDetailsViewModel(storeName: String) : ViewModel() {

    private val repository = OcorrenciasRepository()

    private val _ocorrencias = MutableStateFlow<List<Ocorrencia>>(emptyList())
    val ocorrencias = _ocorrencias.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getOcorrenciasForStore(storeName).collect { _ocorrencias.value = it }
        }
    }

    fun updateOcorrenciaStatus(ocorrenciaId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateStatus(ocorrenciaId, newStatus)
        }
    }
}