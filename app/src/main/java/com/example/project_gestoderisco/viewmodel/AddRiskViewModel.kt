package com.example.project_gestoderisco.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.model.Risk
import com.example.project_gestoderisco.repository.RiskRepository
import kotlinx.coroutines.launch

class AddRiskViewModel(private val repository: RiskRepository) : ViewModel() {

    private val _saveResult = MutableLiveData<Result<Boolean>>()
    val saveResult: LiveData<Result<Boolean>> = _saveResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun saveRisk(
        name: String,
        description: String,
        probability: Int,
        impact: Int,
        impactAnalysis: String,
        mitigationActions: String
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val risk = Risk(0, name, description, probability, impact, impactAnalysis, mitigationActions)
                repository.saveRisk(risk)
                _saveResult.value = Result.success(true)
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}