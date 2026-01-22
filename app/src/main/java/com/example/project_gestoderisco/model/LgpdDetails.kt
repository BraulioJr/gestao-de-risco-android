package com.example.project_gestoderisco.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LgpdDetails(
    val affectsPersonalData: Boolean = false,
    val dataCategories: List<String> = emptyList()
) : Parcelable