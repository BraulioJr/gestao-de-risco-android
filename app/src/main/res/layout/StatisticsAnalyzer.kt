package com.example.gestaoderisco.analysis

import com.example.gestaoderisco.models.Ocorrencia
import java.util.Calendar
import java.util.Date

/**
 * Classe responsável por processar dados de ocorrências e gerar estatísticas
 * para o Dashboard de Prevenção de Perdas.
 */
class StatisticsAnalyzer(private val ocorrencias: List<Ocorrencia>) {

    /**
     * Retorna um mapa com a contagem de ocorrências por "Área" (Tipo de Produto).
     * Útil para alimentar gráficos de distribuição.
     */
    fun getStatisticsByArea(): Map<String, Int> {
        return ocorrencias
            .groupingBy { it.produtos }
            .eachCount()
            .toList()
            .sortedByDescending { (_, count) -> count }
            .toMap()
    }

    /**
     * Retorna um mapa com a contagem de ocorrências por "Loja".
     * Útil para alimentar gráficos de barras comparativos.
     */
    fun getStatisticsByStore(): Map<String, Int> {
        return ocorrencias
            .groupingBy { it.loja }
            .eachCount()
            .toList()
            .sortedByDescending { (_, count) -> count }
            .toMap()
    }

    /**
     * Retorna um mapa com o valor total de prejuízo por "Loja".
     * Útil para análise econômica.
     */
    fun getEconomicAnalysisByStore(): Map<String, Double> {
        return ocorrencias
            .groupBy { it.loja }
            .mapValues { (_, list) -> list.sumOf { it.valor } }
            .toList()
            .sortedByDescending { (_, value) -> value }
            .toMap()
    }

    /**
     * Retorna um mapa com a evolução diária do prejuízo financeiro.
     * Chave: Timestamp do dia (zerado horas/minutos para agrupar).
     * Valor: Soma do prejuízo naquele dia.
     */
    fun getDailyFinancialEvolution(): Map<Long, Double> {
        return ocorrencias
            .groupBy {
                val cal = Calendar.getInstance().apply { time = it.data }
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                cal.timeInMillis
            }
            .mapValues { (_, list) -> list.sumOf { it.valor } }
            .toSortedMap()
    }

    /**
     * Retorna uma lista de pares (Hora do Dia, Valor) para o gráfico de dispersão.
     * X: Hora do dia (ex: 14.5 para 14:30)
     * Y: Valor do furto
     */
    fun getScatterData(): List<Pair<Float, Double>> {
        return ocorrencias.map {
            val cal = Calendar.getInstance().apply { time = it.data }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            val timeFloat = hour + (minute / 60f)
            Pair(timeFloat, it.valor)
        }
    }

    /**
     * Identifica o produto/categoria mais furtado em uma loja específica.
     */
    fun getMostStolenProductByStore(numeroLoja: String): Pair<String, Int>? {
        return ocorrencias
            .filter { it.loja.contains(numeroLoja, ignoreCase = true) }
            .groupingBy { it.produtos }
            .eachCount()
            .maxByOrNull { it.value }
            ?.toPair()
    }

    /**
     * Identifica o produto mais furtado em um determinado período (Dia, Mês ou Ano).
     * @param date Data de referência.
     * @param calendarField Campo do Calendar (Calendar.DAY_OF_YEAR, Calendar.MONTH, Calendar.YEAR).
     */
    fun getMostStolenProductByDate(date: Date, calendarField: Int): Pair<String, Int>? {
        val targetCal = Calendar.getInstance().apply { time = date }

        return ocorrencias
            .filter { ocorrencia ->
                val itemCal = Calendar.getInstance().apply { time = ocorrencia.data }
                when (calendarField) {
                    Calendar.YEAR -> {
                        itemCal.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR)
                    }
                    Calendar.MONTH -> {
                        itemCal.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR) &&
                        itemCal.get(Calendar.MONTH) == targetCal.get(Calendar.MONTH)
                    }
                    Calendar.DAY_OF_YEAR -> {
                        isSameDay(itemCal, targetCal)
                    }
                    else -> false
                }
            }
            .groupingBy { it.produtos }
            .eachCount()
            .maxByOrNull { it.value }
            ?.toPair()
    }

    /**
     * Calcula o valor total recuperado em um determinado período (Dia, Mês ou Ano).
     * @param date Data de referência.
     * @param calendarField Campo do Calendar (Calendar.DAY_OF_YEAR, Calendar.MONTH, Calendar.YEAR).
     */
    fun getTotalValueByDate(date: Date, calendarField: Int): Double {
        val targetCal = Calendar.getInstance().apply { time = date }

        return ocorrencias
            .filter { ocorrencia ->
                val itemCal = Calendar.getInstance().apply { time = ocorrencia.data }
                when (calendarField) {
                    Calendar.YEAR -> {
                        itemCal.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR)
                    }
                    Calendar.MONTH -> {
                        itemCal.get(Calendar.YEAR) == targetCal.get(Calendar.YEAR) &&
                        itemCal.get(Calendar.MONTH) == targetCal.get(Calendar.MONTH)
                    }
                    Calendar.DAY_OF_YEAR -> {
                        isSameDay(itemCal, targetCal)
                    }
                    else -> false
                }
            }
            .sumOf { it.valor }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}