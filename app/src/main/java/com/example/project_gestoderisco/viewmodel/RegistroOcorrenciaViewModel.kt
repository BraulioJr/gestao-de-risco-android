package com.example.project_gestoderisco.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ATENÇÃO: Esta é uma versão revertida e simplificada para restaurar a funcionalidade do projeto.
// A lógica do banco de dados local foi removida temporariamente.
class RegistroOcorrenciaViewModel : ViewModel() {

    private val _registroStatus = MutableLiveData<String>()
    val registroStatus: LiveData<String> = _registroStatus

    // Lista de lojas simulada
    val lojasDisponiveis = listOf("Loja 01 - Centro", "Loja 08 - Shopping", "Loja 15 - Via Norte")

    fun registrarOcorrencia(
        lojaNome: String,
        valorTexto: String,
        produtos: String,
        detalheMonitoramento: String,
        tipoAcao: String,
        relato: String
    ) {
        val valor = valorTexto.toDoubleOrNull()
        if (lojaNome.isEmpty() || valor == null || valor <= 0 || produtos.isEmpty() || relato.isEmpty()) {
            _registroStatus.value = "ERRO: Preencha todos os campos obrigatórios."
            return
        }

        viewModelScope.launch {
            _registroStatus.postValue("PROCESSANDO")
            // Simulação de chamada de rede em vez de salvar no banco de dados
            delay(1500)
            _registroStatus.postValue("SUCESSO: Ocorrência registrada! (Simulado)")
        }
    }
}