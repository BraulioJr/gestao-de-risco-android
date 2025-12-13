package com.example.gestaoderisco.view

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestaoderisco.adapter.OcorrenciaAdapter
import com.example.gestaoderisco.databinding.ActivityOcorrenciasSalvasBinding
import com.example.gestaoderisco.viewmodel.OcorrenciasSalvasViewModel
import com.example.gestaoderisco.viewmodel.OcorrenciasSalvasViewModelFactory

class OcorrenciasSalvasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOcorrenciasSalvasBinding
    private val viewModel: OcorrenciasSalvasViewModel by viewModels {
        OcorrenciasSalvasViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcorrenciasSalvasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Ocorrências Salvas"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = OcorrenciaAdapter { ocorrencia ->
            val intent = Intent(this, OcorrenciaDetailActivity::class.java).apply {
                putExtra("OCORRENCIA_ID", ocorrencia.id)
            }
            startActivity(intent)
        }
        binding.rvOcorrencias.layoutManager = LinearLayoutManager(this)
        binding.rvOcorrencias.adapter = adapter

        viewModel.allOcorrencias.observe(this) { ocorrencias ->
            adapter.submitList(ocorrencias)
            if (ocorrencias.isEmpty()) {
                binding.tvEmptyList.visibility = View.VISIBLE
            } else {
                binding.tvEmptyList.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}