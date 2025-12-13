package com.example.project_gestoderisco

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

object FirebaseEmulatorSetup {

    fun connect() {
        // O IP 10.0.2.2 é um alias especial que o emulador do Android usa
        // para se referir ao localhost da máquina hospedeira.
        val host = "10.0.2.2"

        // Configura o emulador do Firebase Auth
        // Porta padrão: 9099
        Firebase.auth.useEmulator(host, 9099)

        // Configura o emulador do Cloud Firestore
        // Porta alterada para: 8082
        val firestore = Firebase.firestore
        firestore.useEmulator(host, 8082)
        val settings = firestoreSettings {
            isPersistenceEnabled = false
            host = "$host:8082"
            isSslEnabled = false
        }
        firestore.firestoreSettings = settings

        // Configura o emulador do Cloud Storage
        // Porta padrão: 9199
        Firebase.storage.useEmulator(host, 9199)
    }
}