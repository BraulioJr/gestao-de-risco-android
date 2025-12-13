package com.example.project_gestoderisco

import android.app.Application

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            FirebaseEmulatorSetup.connect()
        }
    }
}