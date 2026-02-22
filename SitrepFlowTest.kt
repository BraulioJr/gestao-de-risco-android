package com.example.gestaoderisco

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gestaoderisco.utils.TacticalUtils
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SitrepFlowTest {

    /**
     * Valida o fluxo completo de Inteligência:
     * 1. Geração do Relatório (ReconActivity)
     * 2. Criptografia (TacticalUtils)
     * 3. Decodificação (SitrepDecoderActivity)
     */
    @Test
    fun testFluxoCompletoSitrep() {
        // 1. Simulação: Operador em Campo gera SITREP
        val dadosOriginais = """
            --- SITREP TÁTICO ---
            DTG: ${TacticalUtils.obterDTG()}
            UNIDADE: BRAVO-6
            LOC: -22.9068, -43.1729
            -----------------------
            REGISTRO DE ATIVIDADE (SALUTE):
            10:00 [T] ALVO VISUALIZADO
            10:05 [!] MOVIMENTAÇÃO SUSPEITA
            -----------------------
            STATUS: OPERAÇÃO EM ANDAMENTO
            --- FIM DO RELATÓRIO ---
        """.trimIndent()

        // 2. Simulação: Envio Seguro (Criptografia AES-128)
        val relatorioCifrado = TacticalUtils.criptografarLog(dadosOriginais)

        // Verificações de Segurança
        assertNotEquals("O relatório deve estar cifrado", dadosOriginais, relatorioCifrado)
        assertFalse("Não deve vazar texto plano", relatorioCifrado.contains("BRAVO-6"))

        // 3. Simulação: Coordenador Recebe e Decifra
        val relatorioDecifrado = TacticalUtils.descriptografarLog(relatorioCifrado)

        // 4. Validação de Integridade
        assertEquals("O relatório decifrado deve ser íntegro", dadosOriginais, relatorioDecifrado)
    }
}