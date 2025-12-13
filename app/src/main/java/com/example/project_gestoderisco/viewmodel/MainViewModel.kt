package com.example.project_gestoderisco.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.repository.NotificationRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    // StateFlow para comunicar o resultado da operação para a UI.
    private val _notificationResult = MutableStateFlow<Result<Unit>?>(null)
    val notificationResult = _notificationResult.asStateFlow()

    // Controla o estado de carregamento inicial.
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        // Inicia a verificação assim que o ViewModel é criado.
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Simula um tempo de carregamento para tarefas como verificar o usuário
                // ou carregar configurações iniciais.
                // Em um app real, você faria aqui sua chamada ao Firebase.
                delay(1500)

            } finally {
                // Ao final do processo, define o carregamento como concluído.
                _isLoading.value = false
            }
        }
    }

    /**
     * Aciona o envio de uma notificação de risco usando o repositório.
     */
    fun triggerRiskNotification(title: String, body: String) {
        viewModelScope.launch {
            _notificationResult.value = notificationRepository.sendRiskNotification(title, body)
        }
    }

    /**
     * Limpa o estado do resultado da notificação após ter sido consumido pela UI.
     */
    fun clearNotificationResult() {
        _notificationResult.value = null
    }
}