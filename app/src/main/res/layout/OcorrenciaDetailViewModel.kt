package com.example.gestaoderisco.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.gestaoderisco.data.local.AppDatabase
import com.example.gestaoderisco.data.local.Ocorrencia

class OcorrenciaDetailViewModel(application: Application, ocorrenciaId: String) : AndroidViewModel(application) {
    private val ocorrenciaDao = AppDatabase.getDatabase(application).ocorrenciaDao()
    val ocorrencia: LiveData<Ocorrencia> = ocorrenciaDao.getOcorrenciaById(ocorrenciaId).asLiveData()
}

class OcorrenciaDetailViewModelFactory(
    private val application: Application,
    private val ocorrenciaId: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OcorrenciaDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OcorrenciaDetailViewModel(application, ocorrenciaId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}