package com.example.gestaoderisco.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReportSchedulerUtilsTest {

    @Test
    fun `calculateInitialDelay deve agendar para o mesmo dia se o horario ainda nao passou`() {
        // Cenário: Hoje é Segunda (Calendar.MONDAY) às 08:00
        // Alvo: Segunda às 10:00
        val now = createCalendar(Calendar.MONDAY, 8, 0)
        
        val delay = ReportSchedulerUtils.calculateInitialDelay(
            now.timeInMillis,
            Calendar.MONDAY,
            10, 0
        )

        // Esperado: 2 horas de delay
        assertEquals(TimeUnit.HOURS.toMillis(2), delay)
    }

    @Test
    fun `calculateInitialDelay deve agendar para a proxima semana se o horario ja passou no mesmo dia`() {
        // Cenário: Hoje é Segunda às 10:00
        // Alvo: Segunda às 08:00
        val now = createCalendar(Calendar.MONDAY, 10, 0)

        val delay = ReportSchedulerUtils.calculateInitialDelay(
            now.timeInMillis,
            Calendar.MONDAY,
            8, 0
        )

        // Esperado: 6 dias e 22 horas (ou 7 dias menos 2 horas)
        val expected = TimeUnit.DAYS.toMillis(7) - TimeUnit.HOURS.toMillis(2)
        assertEquals(expected, delay)
    }

    @Test
    fun `calculateInitialDelay deve agendar para o dia correto na mesma semana`() {
        // Cenário: Hoje é Segunda às 08:00
        // Alvo: Quarta às 08:00
        val now = createCalendar(Calendar.MONDAY, 8, 0)

        val delay = ReportSchedulerUtils.calculateInitialDelay(
            now.timeInMillis,
            Calendar.WEDNESDAY,
            8, 0
        )

        // Esperado: 2 dias exatos
        assertEquals(TimeUnit.DAYS.toMillis(2), delay)
    }

    // Helper para criar data simulada
    private fun createCalendar(dayOfWeek: Int, hour: Int, minute: Int): Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, dayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        // Ajuste para garantir que não estamos pegando uma data passada do sistema real se o teste rodar num dia específico
        // Mas para lógica relativa de diff, o timestamp absoluto importa menos que os campos setados, 
        // desde que o calculateInitialDelay use a mesma base.
        return cal
    }
}