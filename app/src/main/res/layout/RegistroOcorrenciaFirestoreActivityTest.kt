package com.example.gestaoderisco

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasErrorText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.anything
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistroOcorrenciaFirestoreActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegistroOcorrenciaFirestoreActivity::class.java)

    @Test
    fun testHelpDialogIsDisplayed_whenHelpIconIsClicked() {
        // Localiza o ícone de ajuda pelo contentDescription definido no layout XML
        // e realiza o clique.
        onView(withContentDescription(R.string.help_fundada_suspeita_desc))
            .perform(click())

        // Verifica se o título do Dialog (Art. 244 CPP) está visível na tela.
        onView(withText(R.string.help_fundada_suspeita_title))
            .check(matches(isDisplayed()))

        // Verifica se a mensagem explicativa do Dialog está visível.
        onView(withText(R.string.help_fundada_suspeita_message))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRegistrationBlocked_whenAbordagemSelectedAndFundadaSuspeitaEmpty() {
        // 1. Seleciona uma categoria válida (índice 1) para passar na primeira validação
        onView(withId(R.id.spn_categoria)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        // 2. Seleciona a ação "Abordagem"
        onView(withId(R.id.rb_abordagem)).perform(click())

        // 3. Clica em "Registrar" sem preencher a Fundada Suspeita
        onView(withId(R.id.btn_registrar)).perform(click())

        // 4. Verifica se o erro é exibido no campo apropriado, confirmando o bloqueio
        onView(withId(R.id.et_fundada_suspeita))
            .check(matches(hasErrorText("Obrigatório descrever a fundada suspeita para abordagens.")))
    }
}