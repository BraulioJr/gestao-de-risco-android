package com.example.gestaoderisco.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gestaoderisco.data.AppDatabase
import com.example.gestaoderisco.models.ReconLog
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReconViewModel(context: Context) : ViewModel() {
    private val dao = AppDatabase.getDatabase(context).reconLogDao()

    val logs = dao.getAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addLog(message: String, type: String, attachmentPath: String? = null) {
        viewModelScope.launch {
            dao.insert(ReconLog(timestamp = System.currentTimeMillis(), message = message, type = type, attachmentPath = attachmentPath))
        }
    }
}

class ReconViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReconViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReconViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}