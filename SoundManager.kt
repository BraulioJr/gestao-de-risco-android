package com.example.project_gestoderisco.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador centralizado de efeitos sonoros (SFX) para o ecossistema de gamificação.
 * Utiliza SoundPool para baixa latência e ToneGenerator como fallback tático.
 */
@Singleton
class SoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val soundPool: SoundPool
    private val soundMap = mutableMapOf<GameSound, Int>()
    
    // Fallback para prototipagem rápida sem assets (Sons sintéticos)
    private val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    enum class GameSound {
        RANK_S,     // Sucesso Elite (Missão Cumprida)
        RANK_B,     // Sucesso Padrão
        RANK_D,     // Falha Crítica / Alerta
        UI_CLICK,   // Feedback Tátil de Interface
        TICK        // Contagem Regressiva
    }

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5) // Permite até 5 sons simultâneos
            .setAudioAttributes(audioAttributes)
            .build()
            
        // TODO: Quando tiver os arquivos finais, coloque em res/raw/ e descomente:
        // loadSound(GameSound.RANK_S, R.raw.sfx_mission_complete)
        // loadSound(GameSound.RANK_D, R.raw.sfx_alert_critical)
    }

    fun loadSound(type: GameSound, resourceId: Int) {
        soundMap[type] = soundPool.load(context, resourceId, 1)
    }

    fun play(sound: GameSound) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val isSoundEnabled = prefs.getBoolean("sound_enabled", true)
        val isVibrationEnabled = prefs.getBoolean("vibration_enabled", true)

        if (isSoundEnabled) {
            val soundId = soundMap[sound]
            // Se o som foi carregado (tem asset), toca via SoundPool. Senão, usa ToneGenerator.
            if (soundId != null && soundId != 0) {
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            } else {
                playFallback(sound)
            }
        }
        if (isVibrationEnabled) {
            playVibration(sound)
        }
    }

    private fun playFallback(sound: GameSound) {
        try {
            when (sound) {
                GameSound.RANK_S -> toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 600)
                GameSound.RANK_B -> toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 400)
                GameSound.RANK_D -> toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 800)
                GameSound.UI_CLICK -> toneGen.startTone(ToneGenerator.TONE_DTMF_0, 50)
                GameSound.TICK -> toneGen.startTone(ToneGenerator.TONE_CDMA_PIP, 100)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playVibration(sound: GameSound) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            val effect = when (sound) {
                GameSound.RANK_S -> VibrationEffect.createWaveform(longArrayOf(0, 80, 50, 80), -1) // Sucesso: Pulso duplo rápido
                GameSound.RANK_B -> VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE) // Padrão
                GameSound.RANK_D -> VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE) // Alerta longo
                GameSound.UI_CLICK -> VibrationEffect.createOneShot(15, 80) // Click sutil
                GameSound.TICK -> VibrationEffect.createOneShot(10, 50) // Tick muito sutil
            }
            vibrator.vibrate(effect)
        }
    }
    
    fun release() {
        soundPool.release()
        toneGen.release()
    }
}