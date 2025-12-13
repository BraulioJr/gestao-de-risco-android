package com.example.project_gestoderisco

import android.app.Application
import com.example.project_gestoderisco.util.ThemeManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializa o ThemeManager e aplica o tema salvo.
        ThemeManager.init(this)
        ThemeManager.applyThemeOnAppStart()
    }
}