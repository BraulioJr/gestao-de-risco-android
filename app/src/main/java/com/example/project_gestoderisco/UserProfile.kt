package com.example.project_gestoderisco.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize // <-- ADICIONE 'import' AQUI

@Parcelize
data class UserProfile(
    val nomeCompleto: String?,
    val email: String?,
    val perfilAcesso: String?
) : Parcelable

