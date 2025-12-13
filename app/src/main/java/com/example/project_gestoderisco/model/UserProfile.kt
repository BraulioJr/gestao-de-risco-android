package com.example.project_gestoderisco.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val uid: String = "",
    val matricula: String? = null,
    val nomeCompleto: String? = null,
    val email: String? = null,
    val lojaId: Int? = null,
    val perfilAcesso: String = "FISCAL" // "FISCAL" ou "GESTOR"
) : Parcelable
