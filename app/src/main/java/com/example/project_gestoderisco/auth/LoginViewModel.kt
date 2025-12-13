package com.example.project_gestoderisco.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_gestoderisco.model.UserProfile
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val profile: UserProfile) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val repo: AuthRepository = AuthRepository()) : ViewModel() {

    private val _uiState = MutableLiveData<LoginUiState>(LoginUiState.Idle)
    val uiState: LiveData<LoginUiState> = _uiState

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("E-mail e senha devem ser preenchidos")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val res = repo.signInWithEmail(email, password)
            if (res.isSuccess) {
                // buscar perfil no Firestore
                val uid = res.getOrNull()!!.uid
                val profileRes = repo.fetchUserProfile(uid)
                if (profileRes.isSuccess) {
                    _uiState.value = LoginUiState.Success(profileRes.getOrNull()!!)
                } else {
                    _uiState.value = LoginUiState.Error("Usuário autenticado mas perfil não encontrado")
                }
            } else {
                _uiState.value = LoginUiState.Error(res.exceptionOrNull()?.message ?: "Erro de login")
            }
        }
    }

    fun signOut() {
        repo.signOut()
        _uiState.value = LoginUiState.Idle
    }
}