package com.example.gestaoderisco.view

import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @Test
    fun verifyThemeChangeToDarkMode() {
        // 1. Inicia a Activity de Configurações
        val scenario = ActivityScenario.launch(SettingsActivity::class.java)

        // 2. Clica na preferência "Tema" (Texto definido em strings.xml)
        onView(withText("Tema")).perform(click())

        // 3. Clica na opção "Escuro" dentro do diálogo que se abre
        onView(withText("Escuro")).perform(click())

        // Aguarda a sincronização da UI
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        // 4. Verifica se o modo noturno foi definido corretamente no delegate
        scenario.onActivity {
            val currentMode = AppCompatDelegate.getDefaultNightMode()
            assertEquals(
                "O modo noturno deveria estar definido como MODE_NIGHT_YES após a seleção",
                AppCompatDelegate.MODE_NIGHT_YES,
                currentMode
            )
        }
        
        scenario.close()
    }
}