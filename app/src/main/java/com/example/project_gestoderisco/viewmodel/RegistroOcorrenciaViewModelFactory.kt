package com.example.project_gestoderisco.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.project_gestoderisco.data.local.AppDatabase
import com.example.project_gestoderisco.repository.OcorrenciaRepository

class RegistroOcorrenciaViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroOcorrenciaViewModel::class.java)) {
            val dao = AppDatabase.getDatabase(context).ocorrenciaDao()
            val repository = OcorrenciaRepository(dao)
            @Suppress("UNCHECKED_CAST")
            return RegistroOcorrenciaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}