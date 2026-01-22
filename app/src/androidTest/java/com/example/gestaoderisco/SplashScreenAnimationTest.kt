package com.example.gestaoderisco

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashScreenAnimationTest {

    @Test
    fun testSplashAnimationDuration() {
        // Obtém o contexto da aplicação para acessar os recursos
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        // Carrega o Animator a partir do XML definido em res/drawable/logo_path_animation.xml
        val animator = AnimatorInflater.loadAnimator(context, R.drawable.logo_path_animation) as ObjectAnimator

        // Valida se a duração configurada é exatamente 1000ms
        assertEquals("A duração da animação deve ser 1000ms", 1000L, animator.duration)
    }
}