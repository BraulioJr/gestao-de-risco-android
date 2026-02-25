package com.example.project_gestoderisco.utils

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.R]) // Fixa na API 30 para testar a lógica do Vibrator padrão
class SoundManagerTest {

    private lateinit var context: Context
    private lateinit var soundManager: SoundManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        soundManager = SoundManager(context)
    }

    @Test
    fun `play triggers vibration correctly`() {
        // Arrange
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val shadowVibrator = Shadows.shadowOf(vibrator)
        
        // É crucial definir que o hardware existe no Shadow, senão o SoundManager ignora a vibração
        shadowVibrator.setHasVibrator(true)

        // Act
        soundManager.play(SoundManager.GameSound.RANK_S)

        // Assert
        // Verifica se o vibrador foi acionado
        assertTrue("A vibração deveria ter sido acionada para o som RANK_S", shadowVibrator.isVibrating)
    }
}