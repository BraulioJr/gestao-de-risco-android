package com.example.project_gestoderisco

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GestaoDeRiscoApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Hilt inicializa automaticamente
    }
}
