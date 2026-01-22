package com.example.gestaoderisco

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gestaoderisco.models.Loja
import com.example.gestaoderisco.models.Ocorrencia
import com.example.gestaoderisco.viewmodel.RegistroOcorrenciaViewModel
import com.example.gestaoderisco.viewmodel.RegistroOcorrenciaViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.Date

class RegistroOcorrenciaFirestoreActivity : AppCompatActivity() {

    // Mapa para armazenar as lojas carregadas do Firestore para busca rápida.
    // A chave é o número da loja (String), e o valor é o objeto Loja.
    private val mapaDeLojas = mutableMapOf<String, Loja>()

    private lateinit var etNumeroLoja: TextInputEditText
    private lateinit var actvLoja: AutoCompleteTextView
    private lateinit var progressLojas: ProgressBar
    private lateinit var etValor: TextInputEditText
    private lateinit var spnCategoria: Spinner
    private lateinit var rgAcao: RadioGroup
    private lateinit var tilFundadaSuspeita: TextInputLayout
    private lateinit var etFundadaSuspeita: TextInputEditText
    private lateinit var etRelato: TextInputEditText
    private lateinit var btnRegistrar: Button

    private val viewModel: RegistroOcorrenciaViewModel by viewModels { RegistroOcorrenciaViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_ocorrencia)

        // Inicializa as Views do layout
        etNumeroLoja = findViewById(R.id.et_numero_loja)
        actvLoja = findViewById(R.id.actv_loja)
        progressLojas = findViewById(R.id.progress_lojas)
        etValor = findViewById(R.id.et_valor)
        spnCategoria = findViewById(R.id.spn_categoria)
        rgAcao = findViewById(R.id.rg_acao)
        tilFundadaSuspeita = findViewById(R.id.til_fundada_suspeita)
        etFundadaSuspeita = findViewById(R.id.et_fundada_suspeita)
        etRelato = findViewById(R.id.et_relato)
        btnRegistrar = findViewById(R.id.btn_registrar)

        setupObservers()
        setupCategorySpinner()
        // Configura o "ouvinte" para o campo de número da loja
        setupStoreNumberListener()
        setupHelpDialog()

        btnRegistrar.setOnClickListener { registrarOcorrencia() }
    }

    private fun setupHelpDialog() {
        tilFundadaSuspeita.setEndIconOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.help_fundada_suspeita_title))
                .setMessage(getString(R.string.help_fundada_suspeita_message))
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

    private fun setupCategorySpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.product_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spnCategoria.adapter = adapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                progressLojas.visibility = if (isLoading) View.VISIBLE else View.GONE
                etNumeroLoja.isEnabled = !isLoading
                btnRegistrar.isEnabled = !isLoading
            }
        }

        lifecycleScope.launch {
            viewModel.lojasState.collect { lojas ->
                mapaDeLojas.clear()
                lojas.forEach { loja ->
                    mapaDeLojas[loja.numero] = loja
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                if (error != null) {
                    Toast.makeText(this@RegistroOcorrenciaFirestoreActivity, error, Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.saveSuccess.collect { success ->
                if (success) {
                    Toast.makeText(this@RegistroOcorrenciaFirestoreActivity, "Ocorrência registrada com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setupStoreNumberListener() {
        etNumeroLoja.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val numeroDigitado = s.toString()

                // Busca o objeto Loja completo no mapa que está em memória
                val lojaEncontrada: Loja? = mapaDeLojas[numeroDigitado]

                if (lojaEncontrada != null) {
                    // Se encontrou, formata um texto descritivo e preenche o campo
                    val textoLojaUnidade = "${lojaEncontrada.nome} - ${lojaEncontrada.endereco}"
                    actvLoja.setText(textoLojaUnidade, false)
                } else {
                    // Se o número digitado não existe no mapa, limpa o campo
                    actvLoja.text.clear()
                }
            }
        })
    }

    private fun registrarOcorrencia() {
        val loja = actvLoja.text.toString()
        val valorStr = etValor.text.toString()
        val categoria = spnCategoria.selectedItem.toString()
        val fundadaSuspeita = etFundadaSuspeita.text.toString()
        val relato = etRelato.text.toString()

        if (spnCategoria.selectedItemPosition == 0) {
            Toast.makeText(this, getString(R.string.error_select_category), Toast.LENGTH_SHORT).show()
            return
        }

        val selectedId = rgAcao.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Selecione uma ação realizada", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedRadioButton = findViewById<RadioButton>(selectedId)
        val acao = selectedRadioButton.text.toString()

        // Lógica da Fundada Suspeita (Art. 244 CPP):
        // Se a ação for Abordagem, a descrição dos fatos (fundada suspeita) é obrigatória para autorizar o registro.
        if (acao.equals(getString(R.string.action_approach), ignoreCase = true) && fundadaSuspeita.isBlank()) {
            etFundadaSuspeita.error = "Obrigatório descrever a fundada suspeita para abordagens."
            Toast.makeText(this, "Para Abordagem, a Fundada Suspeita é obrigatória.", Toast.LENGTH_LONG).show()
            return
        }

        if (loja.isBlank() || valorStr.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
            return
        }

        val valor = valorStr.replace(",", ".").toDoubleOrNull() ?: 0.0

        val ocorrencia = Ocorrencia(
            loja = loja,
            data = Date(),
            valor = valor,
            produtos = categoria,
            acao = acao,
            status = "Aberto", // Status inicial para todo novo caso
            fundadaSuspeita = fundadaSuspeita,
            relato = relato,
            isSynced = false // Será true quando o backend confirmar ou se salvarmos direto online
        )

        viewModel.salvarOcorrencia(ocorrencia)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetSaveState()
    }
}
