package com.example.gestaoderisco.utils

import java.util.Calendar

object ReportSchedulerUtils {

    /**
     * Calcula o atraso inicial (em milissegundos) até o próximo dia e horário alvo.
     *
     * @param currentTimeMillis O tempo atual em milissegundos.
     * @param targetDayOfWeek O dia da semana alvo (ex: Calendar.MONDAY).
     * @param targetHour A hora alvo (0-23).
     * @param targetMinute O minuto alvo (0-59).
     * @return O tempo em milissegundos a esperar até a próxima ocorrência.
     */
    fun calculateInitialDelay(
        currentTimeMillis: Long,
        targetDayOfWeek: Int,
        targetHour: Int,
        targetMinute: Int
    ): Long {
        val currentCal = Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
        val currentDay = currentCal.get(Calendar.DAY_OF_WEEK)

        // Configura o calendário alvo com a data de hoje e o horário desejado
        val targetCal = Calendar.getInstance().apply {
            timeInMillis = currentTimeMillis
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Calcula quantos dias adicionar para chegar ao dia da semana desejado
        var daysToAdd = (targetDayOfWeek - currentDay + 7) % 7

        // Se for o mesmo dia, mas o horário já passou, agenda para a próxima semana
        if (daysToAdd == 0 && targetCal.timeInMillis <= currentTimeMillis) {
            daysToAdd = 7
        }

        targetCal.add(Calendar.DAY_OF_YEAR, daysToAdd)

        return targetCal.timeInMillis - currentTimeMillis
    }
}