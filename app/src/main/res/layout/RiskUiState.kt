package com.example.project_gestoderisco.viewmodel

import com.example.project_gestoderisco.model.Risk

sealed interface RiskUiState {
    object Loading : RiskUiState
    data class Success(val risks: List<Risk>) : RiskUiState
    data class Error(val message: String) : RiskUiState
}