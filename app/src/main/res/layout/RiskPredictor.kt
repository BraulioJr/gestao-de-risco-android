package com.example.gestaoderisco.ml

import android.content.Context
import android.content.res.AssetFileDescriptor
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.Calendar

class RiskPredictor(context: Context) {
    private var interpreter: Interpreter? = null
    var isModelLoaded: Boolean = false
        private set

    init {
        try {
            interpreter = Interpreter(loadModelFile(context, "risk_model.tflite"))
            isModelLoaded = true
        } catch (e: Exception) {
            // Modelo não encontrado ou erro ao carregar. O app continuará usando heurística.
            isModelLoaded = false
        }
    }

    private fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
        val fileDescriptor: AssetFileDescriptor = context.assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Realiza a inferência usando o modelo TFLite.
     * Entradas normalizadas:
     * - Hora (0-1)
     * - Dia da Semana (0-1)
     * - Índice da Categoria (0-1)
     *
     * @return Probabilidade (0.0 a 1.0) ou -1.0 se o modelo não estiver carregado.
     */
    fun predict(categoryIndex: Int, totalCategories: Int): Float {
        if (interpreter == null) return -1f

        val cal = Calendar.getInstance()
        // Normalização simples dos inputs (ajuste conforme o treinamento do seu modelo)
        val hour = cal.get(Calendar.HOUR_OF_DAY) / 24f
        val day = (cal.get(Calendar.DAY_OF_WEEK) - 1) / 7f
        val category = if (totalCategories > 0) categoryIndex.toFloat() / totalCategories else 0f

        val input = floatArrayOf(hour, day, category)
        // Supondo output [1, 1] -> Probabilidade
        val output = Array(1) { FloatArray(1) }

        interpreter?.run(input, output)
        return output[0][0]
    }

    fun close() {
        interpreter?.close()
    }
}