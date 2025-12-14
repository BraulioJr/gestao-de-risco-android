package com.example.project_gestoderisco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.model.Risk
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// Define os possíveis estados da UI para a lista de riscos.
sealed interface RiskListUiState {
    data object Loading : RiskListUiState
    data class Success(val risks: List<Risk>) : RiskListUiState
    data class Error(val message: String) : RiskListUiState
}

// Define os critérios de ordenação disponíveis.
enum class SortOrder(val field: String, val direction: Query.Direction) {
    BY_NAME_ASC("name", Query.Direction.ASCENDING),
    BY_DATE_DESC("createdAt", Query.Direction.DESCENDING),
    BY_ID_ASC("__name__", Query.Direction.ASCENDING) // __name__ é o campo especial para o ID do documento
}

class RiskViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var risksListener: ListenerRegistration? = null

    // Expõe o estado da UI para a Activity. Inicia como "Loading".
    private val _uiState = MutableStateFlow<RiskListUiState>(RiskListUiState.Loading)
    val uiState: StateFlow<RiskListUiState> = _uiState

    // Mantém o critério de ordenação atual. O padrão é por data.
    private val _sortOrder = MutableStateFlow(SortOrder.BY_DATE_DESC)
    
    // Mantém o termo de busca atual.
    private val _searchQuery = MutableStateFlow("")

    init {
        // Combina os fluxos de ordenação e busca. A busca será acionada sempre que um deles mudar.
        combine(_sortOrder, _searchQuery) { sortOrder, searchQuery ->
            fetchRisks(sortOrder, searchQuery)
        }.launchIn(viewModelScope)
    }

    fun changeSortOrder(newOrder: SortOrder) {
        _uiState.value = RiskListUiState.Loading
        _sortOrder.value = newOrder
    }

    fun searchRisks(query: String) {
        // Não precisa mostrar Loading para busca, pois é instantâneo no front-end (se os dados já estiverem carregados)
        // ou será tratado pelo fetchRisks.
        _searchQuery.value = query
    }

    private fun fetchRisks(order: SortOrder, query: String) {
        // Remove o listener anterior para evitar múltiplas buscas simultâneas.
        risksListener?.remove()

        // Garante que só buscamos dados se o usuário estiver logado.
        val userId = auth.currentUser?.uid ?: run {
            _uiState.value = RiskListUiState.Error("Usuário não autenticado.")
            return
        }

        var firestoreQuery: Query = db.collection("risks").whereEqualTo("ownerId", userId)

        // A busca por texto no Firestore requer um índice. Uma abordagem mais simples
        // para começar é filtrar no lado do cliente após receber os dados.
        // Para uma busca real no backend, seria necessário um serviço como o Algolia ou um índice composto no Firestore.

        firestoreQuery = firestoreQuery
            .orderBy(order.field, order.direction)

        risksListener = firestoreQuery
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    _uiState.value = RiskListUiState.Error(error.message ?: "Erro ao buscar os dados.")
                    return@addSnapshotListener
                }
                val allRisks = snapshots?.toObjects<Risk>() ?: emptyList()
                val filteredRisks = if (query.isNotBlank()) {
                    allRisks.filter { it.name.contains(query, ignoreCase = true) }
                } else {
                    allRisks
                }
                _uiState.value = RiskListUiState.Success(filteredRisks)
            }
    }

    override fun onCleared() {
        super.onCleared()
        // Garante que o listener seja removido quando o ViewModel for destruído.
        risksListener?.remove()
    }
}