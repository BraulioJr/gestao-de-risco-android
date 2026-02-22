package com.example.gestaoderisco.view

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.gestaoderisco.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SplashActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(SplashActivity::class.java)

    @Test
    fun testSplashToLoginTransition() {
        // A SplashActivity tem um delay fixo de 2000ms.
        // Aguardamos um tempo ligeiramente maior para garantir que a transição ocorra.
        Thread.sleep(2500)

        // Verificamos se um elemento da LoginActivity (o botão de login) está visível.
        // Isso confirma que a transição foi bem-sucedida.
        onView(withId(R.id.btnLogin)).check(matches(isDisplayed()))
    }
}