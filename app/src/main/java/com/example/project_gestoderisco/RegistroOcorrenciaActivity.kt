package com.example.project_gestoderisco

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RegistroOcorrenciaActivity : AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		// Futuramente, você definirá o layout desta tela aqui
		// setContentView(R.layout.activity_registro_ocorrencia)
	}
}

class RegistroOcorrenciaActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_registro_ocorrencia)

		val btnRegistrar = findViewById<Button>(R.id.buttonRegister)

		btnRegistrar.setOnClickListener {
			criarOcorrencia()
		}
	}

	private fun criarOcorrencia() {
		// 1. Referenciar os componentes da UI
		val etLoja = findViewById<TextInputEditText>(R.id.editTextStoreNumber)
		val spinnerCategoria = findViewById<Spinner>(R.id.spinnerProductType)
		val etValor = findViewById<TextInputEditText>(R.id.editTextValue)
		val radioGroupAcao = findViewById<RadioGroup>(R.id.radioGroupAction)
		val etRelato = findViewById<TextInputEditText>(R.id.editTextReport)

		// 2. Validação da Categoria (Índice 0 é "Selecione...")
		if (spinnerCategoria.selectedItemPosition == 0) {
			Toast.makeText(this, getString(R.string.error_select_category), Toast.LENGTH_LONG)
				.show()
			return
		}

		// 3. Captura dos Dados
		val loja = etLoja.text.toString()
		val categoria = spinnerCategoria.selectedItem.toString()

		// Tratamento seguro para o valor numérico (evita crash se estiver vazio)
		val valorString = etValor.text.toString()
		val valor = if (valorString.isNotEmpty()) valorString.toDouble() else 0.0

		// Captura do RadioButton selecionado
		var acaoRealizada = ""
		val selectedRadioId = radioGroupAcao.checkedRadioButtonId
		if (selectedRadioId != -1) {
			val selectedRadioButton = findViewById<RadioButton>(selectedRadioId)
			acaoRealizada = selectedRadioButton.text.toString()
		}

		val relato = etRelato.text.toString()

		// 4. Criação do Objeto Ocorrencia
		val novaOcorrencia = Ocorrencia(
			loja = loja,
			categoriaProduto = categoria,
			valorEstimado = valor,
			acaoRealizada = acaoRealizada,
			relato = relato
		)

		// --- AQUI O OBJETO ESTÁ PRONTO ---
		// Exemplo: Exibir um Toast de confirmação
		Toast.makeText(
			this,
			"Ocorrência registrada: ${novaOcorrencia.categoriaProduto}",
			Toast.LENGTH_SHORT
		).show()

		// Salvar no Room Database usando Coroutines
		lifecycleScope.launch {
			val db = AppDatabase.getDatabase(applicationContext)
			db.ocorrenciaDao().insert(novaOcorrencia)
		}

		// Limpar os campos do formulário para um novo registro
		etLoja.text?.clear()
		spinnerCategoria.setSelection(0) // Volta para "Selecione uma categoria"
		etValor.text?.clear()
		radioGroupAcao.clearCheck()
		etRelato.text?.clear()
		etLoja.requestFocus() // Coloca o foco no primeiro campo novamente
	}
}