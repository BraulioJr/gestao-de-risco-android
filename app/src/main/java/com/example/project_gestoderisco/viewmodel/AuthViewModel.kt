package com.example.project_gestoderisco.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authResult = MutableLiveData<AuthResult>()
    val authResult: LiveData<AuthResult> = _authResult

    fun login(email: String, password: String) {
        _authResult.value = AuthResult(isLoading = true)
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authResult.value = AuthResult(isSuccess = true)
            }
            .addOnFailureListener { e ->
                _authResult.value = AuthResult(error = e.message)
            }
    }

    fun register(email: String, password: String) {
        _authResult.value = AuthResult(isLoading = true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                _authResult.value = AuthResult(isSuccess = true, isRegistration = true)
            }
            .addOnFailureListener { e ->
                _authResult.value = AuthResult(error = e.message, isRegistration = true)
            }
    }
}

data class AuthResult(
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistration: Boolean = false
)