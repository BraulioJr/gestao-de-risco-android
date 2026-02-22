package com.example.gestaoderisco.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Ocorrencia
import java.text.NumberFormat
import java.util.Locale

object TacticalDialogHelper {

    fun showTacticalDetails(context: Context, ocorrencia: Ocorrencia) {
        val builder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_tactical_details, null)

        // Bind views
        val tvStore = view.findViewById<TextView>(R.id.tvStore)
        val tvValue = view.findViewById<TextView>(R.id.tvValue)
        val btnAcknowledge = view.findViewById<Button>(R.id.btnAcknowledge)

        // --- ANIMAÇÃO DE ALERTA (BLINK) ---
        val headerAnimator = ObjectAnimator.ofFloat(view.findViewById(R.id.tvHeader), "alpha", 1f, 0.3f, 1f)
        headerAnimator.duration = 1200
        headerAnimator.repeatCount = ValueAnimator.INFINITE
        headerAnimator.start()

        // Populate data
        tvStore.text = ocorrencia.loja.uppercase()
        
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        tvValue.text = currencyFormat.format(ocorrencia.valorEstimado)

        builder.setView(view)
        val dialog = builder.create()
        
        // Fundo transparente para respeitar o CornerRadius do CardView
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        btnAcknowledge.setOnClickListener {
            dialog.dismiss()
        }

        // Efeito Sonoro Tático (Bip curto)
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
            toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // --- VIBRAÇÃO TÁTICA ---
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            // Padrão: 0ms delay, 100ms vibra, 100ms pausa, 300ms vibra (SOS curto)
            val timings = longArrayOf(0, 100, 100, 300)
            val amplitudes = intArrayOf(0, 255, 0, 255) // Força máxima

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(timings, -1)
            }
        }

        dialog.show()
    }
}