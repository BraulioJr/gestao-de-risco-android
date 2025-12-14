package com.example.project_gestoderisco

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_gestoderisco.adapter.RiskAdapter
import com.example.project_gestoderisco.databinding.ActivityMainBinding
import com.example.project_gestoderisco.model.Risk
import com.example.project_gestoderisco.view.DashboardActivity
import com.example.project_gestoderisco.view.LoginActivity
import com.example.project_gestoderisco.view.RiskDetailActivity
import com.example.project_gestoderisco.viewmodel.RiskListUiState
import com.example.project_gestoderisco.viewmodel.SortOrder
import com.example.project_gestoderisco.viewmodel.RiskViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: RiskViewModel by viewModels()
    private lateinit var riskAdapter: RiskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupRecyclerView()
        setupListeners()
        observeUiState()
        askNotificationPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // A busca já acontece em tempo real, não precisa fazer nada aqui.
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchRisks(newText.orEmpty())
                return true
            }
        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_dashboard -> {
                startActivity(Intent(this, DashboardActivity::class.java))
                true
            }
            R.id.action_sort_by_name -> {
                viewModel.changeSortOrder(SortOrder.BY_NAME_ASC)
                true
            }
            R.id.action_sort_by_date -> {
                viewModel.changeSortOrder(SortOrder.BY_DATE_DESC)
                true
            }
            R.id.action_sort_by_id -> {
                viewModel.changeSortOrder(SortOrder.BY_ID_ASC)
                true
            }
            R.id.action_logout -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        riskAdapter = RiskAdapter { risk ->
            // Ação ao clicar em um item: abrir tela de detalhes
            val intent = Intent(this, RiskDetailActivity::class.java)
            intent.putExtra("RISK_ID", risk.id) // Passa o ID do risco para a próxima tela
            startActivity(intent)
        }
        binding.recyclerViewRisks.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = riskAdapter
        }
    }

    private fun setupListeners() {
        binding.fabAddRisk.setOnClickListener {
            // Ação do botão FAB: abrir tela de detalhes para criar um novo risco
            startActivity(Intent(this, RiskDetailActivity::class.java))
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progressBar.visibility = if (state is RiskListUiState.Loading) View.VISIBLE else View.GONE

                    when (state) {
                        is RiskListUiState.Success -> {
                            binding.textViewEmptyList.visibility = if (state.risks.isEmpty()) View.VISIBLE else View.GONE
                            binding.recyclerViewRisks.visibility = if (state.risks.isEmpty()) View.GONE else View.VISIBLE
                            riskAdapter.submitList(state.risks)
                        }
                        is RiskListUiState.Error -> {
                            Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_LONG).show()
                            binding.textViewEmptyList.visibility = View.VISIBLE
                            binding.recyclerViewRisks.visibility = View.GONE
                        }
                        is RiskListUiState.Loading -> {
                            // O ProgressBar já é tratado fora do when
                        }
                    }
                }
            }
        }
    }

    // --- Lógica para permissão de notificação (Android 13+) ---

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Permissão para notificações concedida.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissão para notificações negada.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}