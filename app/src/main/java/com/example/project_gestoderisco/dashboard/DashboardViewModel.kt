package com.example.project_gestoderisco.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.collections.HashMap

// ATENÇÃO: Esta é uma versão revertida e simplificada.
class DashboardViewModel(
    private val repo: DashboardRepository = DashboardRepository()
) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    private val _kpi = MutableLiveData<KPIOverview>()
    val kpi: LiveData<KPIOverview> = _kpi

    private val _ranking = MutableLiveData<List<FiscalRankingItem>>()
    val ranking: LiveData<List<FiscalRankingItem>> = _ranking

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _exportUrl = MutableLiveData<String?>()
    val exportUrl: LiveData<String?> = _exportUrl

    private val functions: FirebaseFunctions = Firebase.functions("southamerica-east1")

    fun loadDashboard(startEpochMs: Long, endEpochMs: Long, lojaId: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val docs = withContext(Dispatchers.IO) {
                    repo.fetchOcorrencias(startEpochMs, endEpochMs, lojaId)
                }
                computeKPIs(docs)
                computeRanking(docs)
                _error.value = null
            } catch (t: Throwable) {
                _error.value = t.message ?: "Erro desconhecido"
            } finally {
                _loading.value = false
            }
        }
    }

    fun exportarOcorrenciasCsv(startEpochMs: Long, endEpochMs: Long, lojaId: Int? = null) {
        viewModelScope.launch {
            _loading.value = true
            val data = HashMap<String, Any?>()
            data["startDate"] = startEpochMs
            data["endDate"] = endEpochMs
            data["lojaId"] = lojaId

            try {
                val result = functions
                    .getHttpsCallable("exportOcorrencias")
                    .call(data)
                    .await()

                val downloadUrl = (result.data as? Map<*, *>)?.get("downloadUrl") as? String
                _exportUrl.value = downloadUrl
            } catch (e: Exception) {
                _error.value = "Falha ao exportar: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun computeKPIs(docs: List<Map<String, Any>>) {
        var totalValor = 0.0
        var total = 0
        var abordagens = 0

        for (doc in docs) {
            val valor = (doc["valorRecuperado"] as? Number)?.toDouble() ?: 0.0
            val tipoAcao = doc["tipoAcao"] as? String ?: ""
            totalValor += valor
            total += 1
            if (tipoAcao == "ABORDAGEM_REALIZADA") abordagens += 1
        }

        val eficiencia = if (total == 0) 0.0 else abordagens.toDouble() / total.toDouble()
        _kpi.postValue(KPIOverview(totalValorRecuperado = totalValor, totalOcorrencias = total, indiceEfetividade = eficiencia))
    }

    private fun computeRanking(docs: List<Map<String, Any>>) {
        data class Agg(var total: Double = 0.0, var qtd: Int = 0, var abordagens: Int = 0)

        val map = mutableMapOf<String, Agg>()
        for (doc in docs) {
            val fiscalRaw = doc["usuarioId"] ?: doc["fiscalId"] ?: continue
            val fiscalId = fiscalRaw.toString()
            val valor = (doc["valorRecuperado"] as? Number)?.toDouble() ?: 0.0
            val tipoAcao = doc["tipoAcao"] as? String ?: ""

            val agg = map.getOrPut(fiscalId) { Agg() }
            agg.total += valor
            agg.qtd += 1
            if (tipoAcao == "ABORDAGEM_REALIZADA") agg.abordagens += 1
        }

        val items = map.map { (fiscalId, agg) ->
            val eficiencia = if (agg.qtd == 0) 0.0 else agg.abordagens.toDouble() / agg.qtd.toDouble()
            FiscalRankingItem(
                fiscalId = fiscalId,
                nome = null, 
                totalRecuperado = agg.total,
                qtdOcorrencias = agg.qtd,
                eficiencia = eficiencia
            )
        }.sortedWith(compareByDescending<FiscalRankingItem> { it.totalRecuperado }.thenByDescending { it.eficiencia })

        _ranking.postValue(items)
    }
}