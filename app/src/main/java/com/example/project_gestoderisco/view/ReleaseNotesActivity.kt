package com.example.project_gestoderisco.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_gestoderisco.R
import com.example.project_gestoderisco.databinding.ActivityReleaseNotesBinding
import com.example.project_gestoderisco.view.adapter.ReleaseNotesAdapter
import com.example.project_gestoderisco.view.adapter.ReleaseUiModel

class ReleaseNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReleaseNotesBinding
    private lateinit var adapter: ReleaseNotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReleaseNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        loadReleaseData()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReleaseNotesAdapter {
            // Ação do botão "Ver Histórico"
            Toast.makeText(this, "Carregando histórico completo...", Toast.LENGTH_SHORT).show()
            // Aqui você poderia navegar para uma tela com o feed XML antigo se desejado
        }
        binding.recyclerViewReleaseNotes.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewReleaseNotes.adapter = adapter
    }

    private fun loadReleaseData() {
        // Constrói a lista de itens para a tela
        val items = mutableListOf<ReleaseUiModel>()

        // 1. Cabeçalho
        items.add(ReleaseUiModel.Header(getString(R.string.release_version_current)))

        // 2. Banner
        items.add(ReleaseUiModel.Banner(
            getString(R.string.release_banner_title),
            getString(R.string.release_banner_desc)
        ))

        // 3. Novidades
        items.add(ReleaseUiModel.Section("🆕 " + getString(R.string.section_new)))
        items.add(ReleaseUiModel.Point("Dashboard interativo com filtros avançados"))
        items.add(ReleaseUiModel.Point("Exportação de relatórios em PDF e CSV"))
        items.add(ReleaseUiModel.Point("Modo escuro automático"))

        // 4. Melhorias
        items.add(ReleaseUiModel.Section("🚀 " + getString(R.string.section_improvements)))
        items.add(ReleaseUiModel.Point("Performance de sincronização 2x mais rápida"))
        items.add(ReleaseUiModel.Point("Interface de registro de ocorrência simplificada"))

        // 5. Correções
        items.add(ReleaseUiModel.Section("🛠 " + getString(R.string.section_fixes)))
        items.add(ReleaseUiModel.Point("Correção no upload de imagens em dispositivos Samsung"))
        items.add(ReleaseUiModel.Point("Ajuste no cálculo de prejuízo total"))

        // 6. Segurança
        items.add(ReleaseUiModel.Section("🔐 " + getString(R.string.section_security)))
        items.add(ReleaseUiModel.Point("Autenticação biométrica obrigatória para áreas sensíveis"))
        items.add(ReleaseUiModel.Point("Criptografia de ponta a ponta nos relatórios"))

        // 7. Rodapé
        items.add(ReleaseUiModel.Footer)

        adapter.submitList(items)
    }
}