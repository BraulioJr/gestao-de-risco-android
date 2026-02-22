package com.example.gestaoderisco.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gestaoderisco.R
import com.example.gestaoderisco.databinding.ActivityReconBinding
import com.example.gestaoderisco.models.ReconLog
import com.example.gestaoderisco.utils.EvidenceVault
import com.example.gestaoderisco.utils.TacticalUtils
import com.example.gestaoderisco.viewmodel.ReconViewModel
import com.example.gestaoderisco.viewmodel.ReconViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReconActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityReconBinding
    private val viewModel: ReconViewModel by viewModels { ReconViewModelFactory(applicationContext) }
    private lateinit var adapter: ReconLogAdapter
    private var currentPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private var lastStealthClickTime: Long = 0
    private var currentLogsList: List<ReconLog> = emptyList()
    
    // Variáveis para Panic Trigger (Shake)
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private var shakeThresholdGravity = 2.7F // Sensibilidade do agito (Padrão)

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // Move a foto para o cofre seguro imediatamente após a captura
            val securePath = currentPhotoPath?.let { path ->
                EvidenceVault.moverParaCofre(applicationContext, path)
            }
            logAction("EVIDÊNCIA FOTOGRÁFICA CAPTURADA", "PHOTO", securePath ?: currentPhotoPath)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReconBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupButtons()
        setupStealthMode()
        observeLogs()
        setupPanicTrigger()
        
        if (savedInstanceState == null) {
            logAction("SISTEMA DE INTELIGÊNCIA INICIADO", "SYSTEM")
        }
    }

    private fun setupRecyclerView() {
        adapter = ReconLogAdapter(emptyList())
        binding.rvReconLog.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.rvReconLog.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnLogMovement.setOnClickListener { logAction("MOVIMENTAÇÃO DETECTADA", "MOVEMENT") }
        binding.btnLogTarget.setOnClickListener { logAction("ALVO VISUALIZADO", "TARGET") }
        binding.btnLogRisk.setOnClickListener { logAction("PONTO DE RISCO MARCADO", "RISK") }
        binding.btnLogAsset.setOnClickListener { logAction("RECURSO IDENTIFICADO", "ASSET") }
        binding.btnLogEquipment.setOnClickListener { logAction("EQUIPAMENTO IDENTIFICADO", "EQUIPMENT") }
        binding.btnRequestQrf.setOnClickListener { solicitarReforcoImediato() }
        binding.fabCamera.setOnClickListener { dispatchTakePictureIntent() }
        
        // GATILHO TÁTICO: Clique longo para gerar SITREP (Situation Report)
        // Metodologia SEAL: Comunicação rápida e padronizada (SALUTE Report)
        binding.fabCamera.setOnLongClickListener {
            generateAndShareSitrep()
            true
        }
    }

    private fun setupStealthMode() {
        binding.btnStealth.setOnClickListener {
            binding.viewStealthOverlay.visibility = View.VISIBLE
            Toast.makeText(this, "MODO FURTIVO ATIVO. Toque duplo para sair.", Toast.LENGTH_SHORT).show()
            vibrateTactical()
        }

        binding.viewStealthOverlay.setOnClickListener {
            val clickTime = System.currentTimeMillis()
            if (clickTime - lastStealthClickTime < 500) {
                binding.viewStealthOverlay.visibility = View.GONE
                vibrateTactical()
            } else {
                // Feedback tátil discreto para confirmar que o app está rodando
                vibrateTactical()
            }
            lastStealthClickTime = clickTime
        }
    }

    /**
     * Manobra Tática: Aciona o Fiscal de Prevenção mais próximo.
     * Calcula qual fiscal está mais perto dentro da loja para abordagem imediata.
     */
    private fun solicitarReforcoImediato() {
        // Simulação de coordenadas da ameaça (Local atual do operador)
        val ameacaLat = -22.9068
        val ameacaLon = -43.1729

        // Simulação de Fiscais de Prevenção na loja (Blue Force Tracking)
        // Em produção, filtrar apenas fiscais com status "EM LOJA" no Firebase
        val fiscais = listOf(
            TacticalUtils.UnidadeTatica("Fiscal Carlos (Piso 1)", -22.9069, -43.1728, "A pé"), // Muito perto
            TacticalUtils.UnidadeTatica("Fiscal Ana (Frente de Loja)", -22.9075, -43.1735, "A pé"),
            TacticalUtils.UnidadeTatica("Fiscal Roberto (Estoque)", -22.9060, -43.1720, "A pé")
        )

        val resultado = TacticalUtils.encontrarUnidadeMaisProxima(ameacaLat, ameacaLon, fiscais)

        resultado?.let { (fiscal, distancia) ->
            // Velocidade média de caminhada rápida em loja (1.5 m/s)
            val eta = TacticalUtils.estimarTempoResposta(distancia, 1.5)
            val msg = "FISCAL ACIONADO: ${fiscal.nome} | DIST: ${distancia.toInt()}m | ETA: $eta"
            logAction(msg, "DEPLOY")
            Toast.makeText(this, "🚨 $msg", Toast.LENGTH_LONG).show()
            simularNotificacaoPush(fiscal.nome, distancia.toInt())
        }
    }

    private fun simularNotificacaoPush(fiscalNome: String, distancia: Int) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "simulated_push_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Simulação de Push",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para simular o recebimento de push pelo fiscal"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("CENTRAL DE SEGURANÇA")
            .setContentText("⚠️ $fiscalNome: Deslocamento prioritário solicitado! ($distancia m)")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("⚠️ $fiscalNome: Deslocamento prioritário solicitado!\nDistância do alvo: $distancia metros.\nMotivo: Atividade Suspeita Detectada."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun setupPanicTrigger() {
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Gatilho Manual Oculto: Clique Longo no botão de Modo Furtivo
        binding.btnStealth.setOnLongClickListener {
            triggerPanicProtocol()
            true
        }
    }

    private fun dispatchTakePictureIntent() {
        try {
            val photoFile = File.createTempFile(
                "RECON_${System.currentTimeMillis()}_",
                ".jpg",
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            )
            currentPhotoPath = photoFile.absolutePath
            currentPhotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                photoFile
            )
            takePicture.launch(currentPhotoUri)
        } catch (e: Exception) {
            Toast.makeText(this, "Erro ao criar arquivo de imagem", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logAction(action: String, type: String, attachment: String? = null) {
        viewModel.addLog(action, type, attachment)
        vibrateTactical()
    }

    private fun observeLogs() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.logs.collect { logs ->
                    currentLogsList = logs // Mantém cache local para o SITREP
                    val formattedLogs = logs.map { log ->
                        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                        val prefix = if (log.type == "PHOTO") "📷 " else ""
                        "[$time] $prefix${log.message}"
                    }
                    adapter.updateList(formattedLogs)
                    if (formattedLogs.isNotEmpty()) {
                        binding.rvReconLog.smoothScrollToPosition(formattedLogs.size - 1)
                    }
                }
            }
        }
    }

    /**
     * Implementação da Metodologia SEAL: SITREP (Situation Report)
     * Estrutura baseada no protocolo SALUTE (Size, Activity, Location, Unit, Time, Equipment)
     * Transforma dados brutos em inteligência acionável para o comando.
     */
    private fun generateAndShareSitrep() {
        if (currentLogsList.isEmpty()) {
            Toast.makeText(this, "Sem dados para SITREP", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("ddHHmm'Z' MMM yy", Locale.US) // Formato Militar (DTG)
        val dtg = dateFormat.format(Date()).uppercase()
        
        val sb = StringBuilder()
        sb.append("--- SITREP TÁTICO ---\n")
        sb.append("DTG: $dtg\n")
        sb.append("UNIDADE: AGENTE DE RISCO\n") // Em produção, pegar do UserProfile
        sb.append("LOC: EM CAMPO\n") // Em produção, pegar GPS
        sb.append("-----------------------\n")
        sb.append("REGISTRO DE ATIVIDADE (SALUTE):\n")
        sb.append("(Tamanho, Atividade, Local, Unidade, Tempo, Equipamento)\n")

        currentLogsList.forEach { log ->
            val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(log.timestamp))
            val marker = when(log.type) {
                "RISK" -> "[!]" // Prioridade Alta
                "TARGET" -> "[T]" // Alvo
                "ASSET" -> "[+]" // Recurso
                "EQUIPMENT" -> "[E]" // Equipamento
                "PHOTO" -> "[CAM]"
                "DEPLOY" -> "[>>]" // Movimentação de Tropa
                else -> "[-]"
            }
            sb.append("$time $marker ${log.message}\n")
        }
        
        sb.append("-----------------------\n")
        sb.append("STATUS: OPERAÇÃO EM ANDAMENTO\n")

        // Enterprise Feature: Assinatura de Integridade (Audit Trail)
        val conteudoParcial = sb.toString()
        val hashIntegridade = TacticalUtils.gerarHashIntegridade(conteudoParcial)
        sb.append("HASH (SHA-256): $hashIntegridade\n")
        sb.append("--- FIM DO RELATÓRIO ---")

        val report = sb.toString()
        
        // Criptografa o relatório completo antes de compartilhar
        val encryptedReport = TacticalUtils.criptografarLog(report)

        // Copia para clipboard (Redundância)
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("SITREP_ENC", encryptedReport)
        clipboard.setPrimaryClip(clip)

        // Intent de Compartilhamento Seguro
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, encryptedReport)
            type = "text/plain"
        }
        
        vibrateTactical() // Feedback de confirmação
        startActivity(Intent.createChooser(sendIntent, "Enviar SITREP via:"))
    }

    private fun vibrateTactical() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        }
    }

    // --- Implementação do SensorEventListener (Panic Trigger) ---

    override fun onResume() {
        super.onResume()
        
        // Carrega sensibilidade das configurações (Padrão: 2.7F)
        val prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val sensitivityStr = prefs.getString("panic_sensitivity", "2.7")
        shakeThresholdGravity = sensitivityStr?.toFloatOrNull() ?: 2.7F

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val gX = x / SensorManager.GRAVITY_EARTH
            val gY = y / SensorManager.GRAVITY_EARTH
            val gZ = z / SensorManager.GRAVITY_EARTH

            // Força g total (1.0 = gravidade normal)
            val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

            if (gForce > shakeThresholdGravity) {
                val now = System.currentTimeMillis()
                // Debounce de 2 segundos para evitar múltiplos disparos
                if (lastShakeTime == 0L || (now - lastShakeTime > 2000)) {
                    lastShakeTime = now
                    triggerPanicProtocol()
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Não utilizado
    }

    private fun triggerPanicProtocol() {
        val success = EvidenceVault.destruirEvidencias(applicationContext)
        if (success) {
            vibrateTactical() // Feedback imediato
            Toast.makeText(this, "⚠️ PROTOCOLO PÂNICO: DADOS DESTRUÍDOS", Toast.LENGTH_LONG).show()
            adapter.updateList(emptyList()) // Limpa visualmente
            finishAffinity() // Fecha o app imediatamente
        }
    }
}

class ReconLogAdapter(private var items: List<String>) : RecyclerView.Adapter<ReconLogAdapter.ViewHolder>() {
    
    fun updateList(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val tv = TextView(parent.context).apply {
            setTextColor(Color.GREEN)
            typeface = Typeface.MONOSPACE
            textSize = 12f
            setPadding(16, 8, 16, 8)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
    }

    override fun getItemCount() = items.size
}