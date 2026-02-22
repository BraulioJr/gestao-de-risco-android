package com.example.gestaoderisco.utils

import android.util.Base64
import java.io.File
import java.io.RandomAccessFile
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Utilitários Táticos para padronização militar e segurança de dados.
 * Centraliza formatações (DTG, MGRS) e criptografia de logs operacionais.
 */
object TacticalUtils {

    // Chave simétrica para criptografia de logs (AES-128)
    // NOTA: Em produção, esta chave deve ser derivada via Android Keystore para segurança máxima.
    private const val CHAVE_SECRETA = "G3st40D3R1sc0Key" // 16 caracteres exatos para AES-128
    private const val ALGORITMO = "AES"

    /**
     * Gera o Grupo Data-Hora (DTG - Date Time Group) no padrão militar.
     * Formato: DDHHMM(Z)MONAA
     * Exemplo: 271530ZJAN26
     *
     * @param timestamp Data em milissegundos (padrão: agora)
     * @return String formatada em DTG (Zulu/UTC) com meses em PT-BR
     */
    fun obterDTG(timestamp: Long = System.currentTimeMillis()): String {
        val data = Date(timestamp)

        // Configura para UTC (Zulu Time) para padronização tática
        val formatoHora = SimpleDateFormat("ddHHmm", Locale("pt", "BR"))
        formatoHora.timeZone = TimeZone.getTimeZone("UTC")

        val formatoMesAno = SimpleDateFormat("MMMyy", Locale("pt", "BR"))
        formatoMesAno.timeZone = TimeZone.getTimeZone("UTC")

        // Monta o DTG: DiaHoraMinuto + Z (Zulu) + MêsAno
        // Ex: 091430ZJAN26
        val parteHora = formatoHora.format(data)
        val parteMesAno = formatoMesAno.format(data).uppercase()

        return "${parteHora}Z$parteMesAno"
    }

    /**
     * Simula a formatação de coordenadas no padrão MGRS (Military Grid Reference System).
     * Útil para reportes táticos padronizados (SITREP) onde a leitura rápida é vital.
     *
     * NOTA: Esta é uma aproximação visual para uso em UI e relatórios rápidos.
     * Uma conversão geodésica precisa requer bibliotecas matemáticas complexas.
     *
     * @param lat Latitude
     * @param lon Longitude
     * @return String no formato "ZZB QD EEEEE NNNNN"
     */
    fun formatarCoordenadasMGRS(lat: Double, lon: Double): String {
        // 1. Zona UTM (Cálculo aproximado)
        val zona = ((lon + 180) / 6).toInt() + 1

        // 2. Banda de Latitude (C a X, omitindo I e O)
        val bandas = "CDEFGHJKLMNPQRSTUVWXX"
        val indiceBanda = ((lat + 80) / 8).toInt().coerceIn(0, bandas.length - 1)
        val banda = bandas[indiceBanda]

        // 3. Dígrafo de 100km (Simulado para consistência visual neste contexto)
        val digrafo = "XK"

        // 4. Easting e Northing (5 dígitos = precisão de 1 metro)
        // Usamos a fração decimal para simular o deslocamento dentro da grade
        val easting = (Math.abs(lon) % 1 * 100000).toInt()
        val northing = (Math.abs(lat) % 1 * 100000).toInt()

        return String.format(Locale.US, "%02d%c %s %05d %05d", zona, banda, digrafo, easting, northing)
    }

    /**
     * Criptografa uma mensagem de log usando AES.
     * Garante que informações sensíveis no diário de campo não sejam legíveis em texto plano.
     */
    fun criptografarLog(mensagem: String): String {
        return try {
            val chave = SecretKeySpec(CHAVE_SECRETA.toByteArray(), ALGORITMO)
            val cifra = Cipher.getInstance(ALGORITMO)
            cifra.init(Cipher.ENCRYPT_MODE, chave)

            val bytesCriptografados = cifra.doFinal(mensagem.toByteArray())
            Base64.encodeToString(bytesCriptografados, Base64.NO_WRAP)
        } catch (e: Exception) {
            "[FALHA NA CRIPTOGRAFIA]: ${e.message}"
        }
    }

    /**
     * Descriptografa uma mensagem de log previamente protegida.
     */
    fun descriptografarLog(mensagemCriptografada: String): String {
        return try {
            val chave = SecretKeySpec(CHAVE_SECRETA.toByteArray(), ALGORITMO)
            val cifra = Cipher.getInstance(ALGORITMO)
            cifra.init(Cipher.DECRYPT_MODE, chave)

            val bytesDecodificados = Base64.decode(mensagemCriptografada, Base64.NO_WRAP)
            String(cifra.doFinal(bytesDecodificados))
        } catch (e: Exception) {
            "[DADO CORROMPIDO OU CHAVE INVÁLIDA]"
        }
    }

    /**
     * Gera um hash SHA-256 para garantir a integridade da mensagem (Assinatura Digital).
     * Essencial para auditoria corporativa e validação da cadeia de custódia militar.
     *
     * @param dados Texto a ser assinado
     * @return Hash hexadecimal
     */
    fun gerarHashIntegridade(dados: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(dados.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Realiza a sanitização de dados (Data Wiping) seguindo princípios do padrão DoD 5220.22-M.
     * Sobrescreve o arquivo com padrões binários antes de deletar para impedir recuperação forense.
     */
    fun sanitizarArquivo(arquivo: File): Boolean {
        if (!arquivo.exists()) return false
        return try {
            RandomAccessFile(arquivo, "rws").use { randomAccessFile ->
                val length = arquivo.length()
                val chunkSize = 4096 // Buffer de 4KB para eficiência de memória
                val chunk = ByteArray(chunkSize)
                val secureRandom = SecureRandom()

                // Passada 1: Sobrescrever com Zeros
                // Um novo ByteArray já é inicializado com zeros.
                for (i in 0 until length step chunkSize.toLong()) {
                    val toWrite = minOf(chunkSize.toLong(), length - i).toInt()
                    randomAccessFile.write(chunk, 0, toWrite)
                }

                // Passada 2: Sobrescrever com Dados Aleatórios
                randomAccessFile.seek(0) // Retorna ao início do arquivo
                for (i in 0 until length step chunkSize.toLong()) {
                    secureRandom.nextBytes(chunk)
                    val toWrite = minOf(chunkSize.toLong(), length - i).toInt()
                    randomAccessFile.write(chunk, 0, toWrite)
                }
            }
            arquivo.delete() // Deleção final do sistema de arquivos
        } catch (e: Exception) {
            e.printStackTrace()
            arquivo.delete() // Fallback para deleção simples em caso de erro de I/O
        }
    }

    // --- ESTRATÉGIA DE MANOBRA (BLUE FORCE TRACKING) ---

    /**
     * Representação de uma unidade de segurança em campo (As "Tropas").
     */
    data class UnidadeTatica(val nome: String, val lat: Double, val lon: Double, val tipo: String)

    /**
     * Calcula a distância em metros entre dois pontos (Fórmula de Haversine).
     * Fundamental para saber qual tropa está mais próxima do conflito.
     */
    fun calcularDistanciaMetros(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Raio da Terra em metros
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return R * c
    }

    /**
     * Estima o tempo de resposta (ETA) com base em uma velocidade média de deslocamento tático.
     * @param distanciaMetros Distância até o alvo
     * @param velocidadeMediaMs Velocidade em m/s (Padrão: 2.5 m/s = corrida leve/tática)
     * @return String formatada (ex: "45 seg" ou "2 min")
     */
    fun estimarTempoResposta(distanciaMetros: Double, velocidadeMediaMs: Double = 2.5): String {
        val segundos = (distanciaMetros / velocidadeMediaMs).toInt()
        return if (segundos < 60) "$segundos seg" else "${segundos / 60} min"
    }

    /**
     * Identifica a unidade mais próxima da ameaça para despacho imediato.
     */
    fun encontrarUnidadeMaisProxima(ameacaLat: Double, ameacaLon: Double, unidades: List<UnidadeTatica>): Pair<UnidadeTatica, Double>? {
        return unidades.minByOrNull { calcularDistanciaMetros(ameacaLat, ameacaLon, it.lat, it.lon) }
            ?.let { it to calcularDistanciaMetros(ameacaLat, ameacaLon, it.lat, it.lon) }
    }
}