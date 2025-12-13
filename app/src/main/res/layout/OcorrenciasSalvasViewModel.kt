package com.example.gestaoderisco.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.gestaoderisco.data.local.AppDatabase

class OcorrenciasSalvasViewModel(application: Application) : AndroidViewModel(application) {
    private val ocorrenciaDao = AppDatabase.getDatabase(application).ocorrenciaDao()
    val allOcorrencias = ocorrenciaDao.getAllOcorrencias().asLiveData()
}

class OcorrenciasSalvasViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OcorrenciasSalvasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OcorrenciasSalvasViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}