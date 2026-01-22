package com.example.gestaoderisco

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.example.gestaoderisco.data.AppDatabase
import com.example.gestaoderisco.model.Ocorrencia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistroOcorrenciaActivity : AppCompatActivity() {

    private var filterStartDate: Long? = null
    private var filterEndDate: Long? = null
    // Variáveis para armazenar GPS (devem ser atualizadas pela lógica de localização)
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private val currentTenantId = "tenant_1" // Em produção, viria da sessão do usuário

    private val exportCsvLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { exportarDadosParaUri(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro_ocorrencia)

        val btnRegistrar = findViewById<Button>(R.id.buttonRegister)
        val btnExportar = findViewById<Button>(R.id.buttonExport)
        val btnDashboard = findViewById<Button>(R.id.buttonDashboard)

        // Verifica se recebeu coordenadas do Mapa (Clique Longo)
        val lat = intent.getDoubleExtra("LATITUDE", 0.0)
        val lon = intent.getDoubleExtra("LONGITUDE", 0.0)
        if (lat != 0.0 && lon != 0.0) {
            currentLatitude = lat
            currentLongitude = lon
            findViewById<TextView>(R.id.textViewGpsCoordinates)?.text = "Lat: $lat, Long: $lon"
        }

        btnRegistrar.setOnClickListener {
            criarOcorrencia()
        }

        btnExportar.setOnClickListener {
            val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Selecione o período")
                .setSelection(androidx.core.util.Pair(MaterialDatePicker.thisMonthInUtcMilliseconds(), MaterialDatePicker.todayInUtcMilliseconds()))
                .build()

            dateRangePicker.addOnPositiveButtonClickListener { selection ->
                filterStartDate = selection.first
                filterEndDate = selection.second + 86399000L 

                val dataAtual = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                exportCsvLauncher.launch("ocorrencias_$dataAtual.csv")
            }

            dateRangePicker.show(supportFragmentManager, "date_export_filter")
        }

        btnDashboard.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun criarOcorrencia() {
        val etLoja = findViewById<TextInputEditText>(R.id.editTextStoreNumber)
        val spinnerCategoria = findViewById<Spinner>(R.id.spinnerProductType)
        val etValor = findViewById<TextInputEditText>(R.id.editTextValue)
        val radioGroupAcao = findViewById<RadioGroup>(R.id.radioGroupAction)
        val etRelato = findViewById<TextInputEditText>(R.id.editTextReport)
        val radioGroupPerfil = findViewById<RadioGroup>(R.id.radioGroupShoplifterType)

        if (spinnerCategoria.selectedItemPosition == 0) {
            Toast.makeText(this, getString(R.string.error_select_category), Toast.LENGTH_LONG).show()
            return
        }

        val loja = etLoja.text.toString()
        val categoria = spinnerCategoria.selectedItem.toString()
        
        val valorString = etValor.text.toString()
        val valor = if (valorString.isNotEmpty()) valorString.toDouble() else 0.0

        var acaoRealizada = ""
        val selectedRadioId = radioGroupAcao.checkedRadioButtonId
        if (selectedRadioId != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedRadioId)
            acaoRealizada = selectedRadioButton.text.toString()
        }

        var perfilFurtante = ""
        val selectedPerfilId = radioGroupPerfil.checkedRadioButtonId
        if (selectedPerfilId != -1) {
            val selectedRadioButton = findViewById<RadioButton>(selectedPerfilId)
            perfilFurtante = selectedRadioButton.text.toString()
        }

        val relato = etRelato.text.toString()

        val novaOcorrencia = Ocorrencia(
            loja = loja,
            categoriaProduto = categoria,
            valorEstimado = valor,
            acaoRealizada = acaoRealizada,
            relato = relato,
            perfilFurtante = perfilFurtante,
            latitude = currentLatitude,
            longitude = currentLongitude,
            tenantId = currentTenantId
        )

        Toast.makeText(this, "Ocorrência registrada: ${novaOcorrencia.categoriaProduto}", Toast.LENGTH_SHORT).show()

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            db.ocorrenciaDao().insert(novaOcorrencia)
        }

        etLoja.text?.clear()
        spinnerCategoria.setSelection(0)
        etValor.text?.clear()
        radioGroupAcao.clearCheck()
        radioGroupPerfil.clearCheck()
        etRelato.text?.clear()
        etLoja.requestFocus()
    }

    private fun exportarDadosParaUri(uri: android.net.Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                
                val lista = if (filterStartDate != null && filterEndDate != null) {
                    db.ocorrenciaDao().getByDateRange(currentTenantId, filterStartDate!!, filterEndDate!!)
                } else {
                    db.ocorrenciaDao().getAll(currentTenantId)
                }

                if (lista.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegistroOcorrenciaActivity, "Nenhum dado encontrado no período.", Toast.LENGTH_LONG).show()
                    }
                    return@launch
                }

                val csvContent = StringBuilder()
                csvContent.append("ID,Loja,Categoria,Valor,Acao,Relato,Data\n")

                lista.forEach { item ->
                    val relatoSeguro = item.relato.replace("\"", "\"\"")
                    csvContent.append("${item.id},${item.loja},${item.categoriaProduto},${item.valorEstimado},${item.acaoRealizada},\"$relatoSeguro\",${item.dataRegistro}\n")
                }

                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    outputStream.write(csvContent.toString().toByteArray())
                }

                filterStartDate = null
                filterEndDate = null

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistroOcorrenciaActivity, getString(R.string.export_success), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegistroOcorrenciaActivity, getString(R.string.export_error), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}