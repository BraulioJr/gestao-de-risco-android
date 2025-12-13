package com.example.gestaoderisco.view

import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gestaoderisco.data.local.Ocorrencia
import com.example.gestaoderisco.databinding.ActivityOcorrenciaDetailBinding
import com.example.gestaoderisco.viewmodel.OcorrenciaDetailViewModel
import com.example.gestaoderisco.viewmodel.OcorrenciaDetailViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class OcorrenciaDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOcorrenciaDetailBinding
    private val viewModel: OcorrenciaDetailViewModel by viewModels {
        OcorrenciaDetailViewModelFactory(application, intent.getStringExtra("OCORRENCIA_ID") ?: "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcorrenciaDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detalhes da Ocorrência"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.ocorrencia.observe(this) { ocorrencia ->
            ocorrencia?.let { popularDados(it) }
        }
    }

    private fun popularDados(ocorrencia: Ocorrencia) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        binding.run {
            tvDetailLoja.text = ocorrencia.loja
            tvDetailData.text = dateFormat.format(ocorrencia.data)
            tvDetailValor.text = String.format(Locale.getDefault(), "R$ %.2f", ocorrencia.valor)
            tvDetailProdutos.text = ocorrencia.produtos
            tvDetailAcao.text = ocorrencia.acao
            tvDetailRelato.text = ocorrencia.relato

            if (ocorrencia.urlEvidencia != null) {
                ivDetailEvidencia.visibility = View.VISIBLE
                Glide.with(this@OcorrenciaDetailActivity)
                    .load(ocorrencia.urlEvidencia)
                    .into(ivDetailEvidencia)
            } else {
                ivDetailEvidencia.visibility = View.GONE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}