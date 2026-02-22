package com.example.gestaoderisco.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TacticalUtilsTest {

    @Test
    fun testCriptografiaFluxoCompleto() {
        val mensagemSecreta = "ALVO LOCALIZADO: 2200 HORAS"
        
        // 1. Criptografar
        val textoCifrado = TacticalUtils.criptografarLog(mensagemSecreta)
        
        // Validações da Cifra
        assertNotEquals("A mensagem não deve ser igual após criptografia", mensagemSecreta, textoCifrado)
        assertTrue("O texto cifrado não deve estar vazio", textoCifrado.isNotEmpty())
        
        // 2. Descriptografar
        val textoDecifrado = TacticalUtils.descriptografarLog(textoCifrado)
        
        // Validação Final
        assertEquals("A mensagem descriptografada deve ser idêntica à original", mensagemSecreta, textoDecifrado)
    }

    @Test
    fun testFormatacaoDTG() {
        // Teste de sanidade do formato DTG (Grupo Data-Hora)
        val dtg = TacticalUtils.obterDTG()
        
        // Verifica se contém o sufixo Zulu (Z) indicando UTC
        assertTrue("DTG deve conter o indicador de fuso Zulu 'Z'", dtg.contains("Z"))
        
        // Verifica se não está vazio
        assertTrue("DTG não deve ser vazio", dtg.isNotEmpty())
        
        // Regex para validar formato DDHHMMZMONAA (Ex: 091430ZJAN26)
        // \d{6} = 6 dígitos (DiaHoraMinuto)
        // Z = Letra Z
        // [A-Z]{3} = 3 Letras (Mês)
        // \d{2} = 2 dígitos (Ano)
        val regex = Regex("\\d{6}Z[A-Z]{3}\\d{2}")
        assertTrue("DTG deve seguir o padrão militar (Ex: 271530ZJAN26). Valor atual: $dtg", dtg.matches(regex))
    }
    
    @Test
    fun testCriptografiaComCaracteresEspeciais() {
        val mensagemComplexa = "SITREP: Ação em São Paulo/SP - R$ 5.000,00 #Crítico"
        val cifrado = TacticalUtils.criptografarLog(mensagemComplexa)
        val decifrado = TacticalUtils.descriptografarLog(cifrado)
        
        assertEquals("A criptografia deve suportar caracteres especiais e acentos", mensagemComplexa, decifrado)
    }
}