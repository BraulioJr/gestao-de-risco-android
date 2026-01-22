package com.example.gestaoderisco.analysis

import com.example.gestaoderisco.models.Ocorrencia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.Calendar
import java.util.Date

class StatisticsAnalyzerTest {

    private lateinit var analyzer: StatisticsAnalyzer
    private lateinit var dateToday: Date
    private lateinit var dateYesterday: Date
    private lateinit var dateLastMonth: Date
    private lateinit var dateLastYear: Date

    @Before
    fun setUp() {
        val cal = Calendar.getInstance()

        // Data 1: Hoje
        dateToday = cal.time

        // Data 2: Ontem
        cal.add(Calendar.DAY_OF_YEAR, -1)
        dateYesterday = cal.time

        // Reset para hoje para calcular mês passado
        cal.time = Date()
        cal.add(Calendar.MONTH, -1)
        dateLastMonth = cal.time

        // Reset para hoje para calcular ano passado
        cal.time = Date()
        cal.add(Calendar.YEAR, -1)
        dateLastYear = cal.time

        val ocorrencias = listOf(
            // Hoje: 2x Picanha, 1x Whisky
            createOcorrencia("Loja 101", dateToday, "Picanha"),
            createOcorrencia("Loja 101", dateToday, "Picanha"),
            createOcorrencia("Loja 101", dateToday, "Whisky"),

            // Ontem: 2x Whisky
            createOcorrencia("Loja 101", dateYesterday, "Whisky"),
            createOcorrencia("Loja 101", dateYesterday, "Whisky"),

            // Mês Passado: 3x Shampoo
            createOcorrencia("Loja 102", dateLastMonth, "Shampoo"),
            createOcorrencia("Loja 102", dateLastMonth, "Shampoo"),
            createOcorrencia("Loja 102", dateLastMonth, "Shampoo"),

            // Ano Passado: 1x TV
            createOcorrencia("Loja 103", dateLastYear, "TV")
        )

        analyzer = StatisticsAnalyzer(ocorrencias)
    }

    @Test
    fun `getStatisticsByArea deve retornar contagem correta e ordenada`() {
        val stats = analyzer.getStatisticsByArea()

        // Total: Picanha(2), Whisky(3), Shampoo(3), TV(1)
        // Esperado: Whisky e Shampoo no topo (3), Picanha (2), TV (1)

        assertEquals(3, stats["Whisky"])
        assertEquals(3, stats["Shampoo"])
        assertEquals(2, stats["Picanha"])
        assertEquals(1, stats["TV"])

        // Verifica se o primeiro elemento tem a maior contagem (3)
        val maxCount = stats.values.first()
        assertEquals(3, maxCount)
    }

    @Test
    fun `getMostStolenProductByStore deve filtrar por loja corretamente`() {
        // Loja 101: Picanha(2), Whisky(3) -> Vencedor: Whisky
        val result101 = analyzer.getMostStolenProductByStore("101")
        assertNotNull(result101)
        assertEquals("Whisky", result101?.first)
        assertEquals(3, result101?.second)

        // Loja 102: Shampoo(3) -> Vencedor: Shampoo
        val result102 = analyzer.getMostStolenProductByStore("102")
        assertNotNull(result102)
        assertEquals("Shampoo", result102?.first)
        assertEquals(3, result102?.second)

        // Loja inexistente
        val result999 = analyzer.getMostStolenProductByStore("999")
        assertNull(result999)
    }

    @Test
    fun `getMostStolenProductByDate deve filtrar por DIA corretamente`() {
        // Hoje: Picanha(2), Whisky(1) -> Vencedor: Picanha
        val resultToday = analyzer.getMostStolenProductByDate(dateToday, Calendar.DAY_OF_YEAR)
        assertNotNull(resultToday)
        assertEquals("Picanha", resultToday?.first)
        assertEquals(2, resultToday?.second)

        // Ontem: Whisky(2) -> Vencedor: Whisky
        val resultYesterday = analyzer.getMostStolenProductByDate(dateYesterday, Calendar.DAY_OF_YEAR)
        assertNotNull(resultYesterday)
        assertEquals("Whisky", resultYesterday?.first)
        assertEquals(2, resultYesterday?.second)
    }

    @Test
    fun `getMostStolenProductByDate deve filtrar por MES corretamente`() {
        // Mês Passado: Shampoo(3)
        val resultLastMonth = analyzer.getMostStolenProductByDate(dateLastMonth, Calendar.MONTH)
        assertNotNull(resultLastMonth)
        assertEquals("Shampoo", resultLastMonth?.first)
        assertEquals(3, resultLastMonth?.second)
    }

    @Test
    fun `getTotalValueByDate deve somar valores corretamente por periodo`() {
        // Hoje: 3 ocorrências de 100.0 cada = 300.0
        val totalToday = analyzer.getTotalValueByDate(dateToday, Calendar.DAY_OF_YEAR)
        assertEquals(300.0, totalToday, 0.001)

        // Mês Passado: 3 ocorrências de 100.0 cada = 300.0
        val totalLastMonth = analyzer.getTotalValueByDate(dateLastMonth, Calendar.MONTH)
        assertEquals(300.0, totalLastMonth, 0.001)
    }

    @Test
    fun `getCriminalSpotData deve agrupar por hora e categoria corretamente`() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 14) // 14:00
        val date14h = cal.time

        val listaTeste = listOf(
            createOcorrencia("Loja A", date14h, "Carnes"),
            createOcorrencia("Loja A", date14h, "Carnes"), // 2 ocorrências as 14h de Carnes
            createOcorrencia("Loja A", date14h, "Bebidas") // 1 ocorrência as 14h de Bebidas
        )

        val analyzerTeste = StatisticsAnalyzer(listaTeste)
        val categorias = arrayOf("Carnes", "Bebidas", "Outros") // Índices: 0, 1, 2

        val result = analyzerTeste.getCriminalSpotData(categorias)

        val carnesSpot = result.find { it.first == 14 && it.second == 0 }
        val bebidasSpot = result.find { it.first == 14 && it.second == 1 }

        assertNotNull("Deveria ter dados para Carnes as 14h", carnesSpot)
        assertEquals(2, carnesSpot?.third)

        assertNotNull("Deveria ter dados para Bebidas as 14h", bebidasSpot)
        assertEquals(1, bebidasSpot?.third)
    }

    private fun createOcorrencia(loja: String, data: Date, produto: String): Ocorrencia {
        return Ocorrencia(
            loja = loja,
            data = data,
            dataRegistro = data, // Em testes antigos, assumimos que o registro foi imediato
            valor = 100.0,
            produtos = produto,
            acao = "Recuperação",
            status = "Resolvido",
            fundadaSuspeita = "Suspeita baseada em comportamento (Teste)",
            relato = "Teste unitário",
            isSynced = true
        )
    }
}