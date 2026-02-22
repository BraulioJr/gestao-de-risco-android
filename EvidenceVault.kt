package com.example.gestaoderisco.utils

import android.content.Context
import java.io.File
import java.io.IOException

/**
 * Cofre de Evidências (Evidence Vault)
 * Responsável por mover arquivos de mídia capturados para uma área de armazenamento
 * interna e "oculta" do aplicativo, dificultando o acesso externo e protegendo a cadeia de custódia.
 */
object EvidenceVault {

    // Nome da pasta oculta (iniciada com ponto) no armazenamento interno
    private const val VAULT_DIR_NAME = ".secure_evidence_vault"

    /**
     * Move uma foto temporária (cache/externa) para o cofre seguro interno.
     *
     * @param context Contexto da aplicação
     * @param sourcePath Caminho absoluto do arquivo temporário
     * @return O novo caminho absoluto do arquivo seguro, ou null em caso de falha.
     */
    fun moverParaCofre(context: Context, sourcePath: String): String? {
        val sourceFile = File(sourcePath)
        if (!sourceFile.exists()) return null

        // Diretório oculto no armazenamento interno (Private Storage)
        // filesDir é privado do app e não acessível por outros apps (sem root)
        val vaultDir = File(context.filesDir, VAULT_DIR_NAME)
        if (!vaultDir.exists()) {
            vaultDir.mkdirs()
        }

        // Mantém o nome do arquivo original (que já contém timestamp)
        val fileName = sourceFile.name
        val destFile = File(vaultDir, fileName)

        return try {
            // Move o arquivo (Copy + Delete) para garantir que saia da área pública
            sourceFile.copyTo(destFile, overwrite = true)
            
            // Limpa o rastro original
            if (sourceFile.exists()) {
                sourceFile.delete()
            }
            
            destFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Lista todas as evidências armazenadas no cofre.
     */
    fun listarEvidencias(context: Context): List<File> {
        val vaultDir = File(context.filesDir, VAULT_DIR_NAME)
        return vaultDir.listFiles()?.toList() ?: emptyList()
    }

    /**
     * Apaga todas as evidências do cofre (Panic Button / Limpeza de Emergência).
     * Utiliza sanitização de dados (sobrescrita) para impedir recuperação forense.
     */
    fun destruirEvidencias(context: Context): Boolean {
        val vaultDir = File(context.filesDir, VAULT_DIR_NAME)
        if (!vaultDir.exists()) return false

        // Sanitiza cada arquivo individualmente antes de remover o diretório
        vaultDir.listFiles()?.forEach { arquivo ->
            TacticalUtils.sanitizarArquivo(arquivo)
        }

        return vaultDir.deleteRecursively()
    }
}