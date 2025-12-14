package com.example.project_gestoderisco.view // O pacote foi corrigido para corresponder à pasta 'view'

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.project_gestoderisco.MainActivity
import com.example.project_gestoderisco.R
import com.example.project_gestoderisco.databinding.ActivityLoginBinding
import com.example.project_gestoderisco.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Se o usuário já estiver logado, vai direto para a MainActivity
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnLogin.setOnClickListener {
            handleAuthAction(isLogin = true)
        }
        binding.btnRegister.setOnClickListener {
            handleAuthAction(isLogin = false)
        }
    }

    private fun handleAuthAction(isLogin: Boolean) {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etSenha.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, getString(R.string.empty_fields), Toast.LENGTH_SHORT).show()
            return
        }

        if (isLogin) {
            authViewModel.login(email, password)
        } else {
            authViewModel.register(email, password)
        }
    }

    private fun setupObservers() {
        authViewModel.authResult.observe(this) { result ->
            binding.progressBar.visibility = if (result.isLoading) View.VISIBLE else View.GONE

            result.error?.let {
                val message = if (result.isRegistration) getString(R.string.registration_failed) else getString(R.string.auth_failed)
                Toast.makeText(this, "$message: $it", Toast.LENGTH_LONG).show()
            }

            if (result.isSuccess) {
                val message = if (result.isRegistration) getString(R.string.registration_success) else getString(R.string.auth_success)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish() // Impede o usuário de voltar para a tela de login
            }
        }
    }
}