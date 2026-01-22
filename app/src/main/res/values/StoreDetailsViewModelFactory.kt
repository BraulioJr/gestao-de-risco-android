package com.example.gestaoderisco.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StoreDetailsViewModelFactory(private val storeName: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StoreDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StoreDetailsViewModel(storeName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}