package com.example.project_gestoderisco

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.project_gestoderisco.databinding.ActivityMainBinding
import com.example.project_gestoderisco.view.RiskDetailActivity
import com.example.project_gestoderisco.view.SettingsActivity
import com.example.project_gestoderisco.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instala e configura a Splash Screen para aguardar o carregamento.
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoading.value
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a Toolbar
        setSupportActionBar(binding.toolbar)

        // Configura o clique do botão para abrir a tela de detalhes do risco
        binding.fabAddRisk.setOnClickListener {
            val intent = Intent(this, RiskDetailActivity::class.java)
            startActivity(intent)
        }

        // Configura o clique do botão para enviar a notificação
        binding.buttonSendNotification.setOnClickListener {
            val title = binding.editTextNotificationTitle.text.toString().trim()
            val body = binding.editTextNotificationBody.text.toString().trim()

            if (title.isNotEmpty() && body.isNotEmpty()) {
                viewModel.triggerRiskNotification(title, body)
            } else {
                Toast.makeText(this, "Por favor, preencha o título e o corpo da notificação.", Toast.LENGTH_SHORT).show()
            }
        }

        // Observa o resultado do envio da notificação para dar feedback ao usuário.
        lifecycleScope.launch {
            viewModel.notificationResult.collectLatest { result ->
                result?.onSuccess {
                    Toast.makeText(this@MainActivity, "Comando de notificação enviado com sucesso!", Toast.LENGTH_SHORT).show()
                }?.onFailure { error ->
                    Toast.makeText(this@MainActivity, "Falha ao enviar comando: ${error.message}", Toast.LENGTH_LONG).show()
                }
                // Limpa o resultado para evitar que o Toast seja exibido novamente (ex: em rotação de tela)
                viewModel.clearNotificationResult()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Infla o menu com a opção de alterar tema.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Trata o clique no item de menu.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}