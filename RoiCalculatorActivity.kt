package com.example.project_gestoderisco.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.project_gestoderisco.databinding.ActivityRoiCalculatorBinding
import java.text.NumberFormat
import java.util.Locale

class RoiCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoiCalculatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoiCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listener para atualizar o texto do slider em tempo real
        binding.sliderResponseTime.addOnChangeListener { _, value, _ ->
            binding.tvResponseTimeValue.text = "${value.toInt()} min"
        }

        binding.btnCalculateRoi.setOnClickListener {
            calculateRoi()
        }
    }

    private fun calculateRoi() {
        val investmentStr = binding.etInvestment.text.toString()
        val prevLossesStr = binding.etPreviousLosses.text.toString()
        val currentLossesStr = binding.etCurrentLosses.text.toString()

        if (investmentStr.isEmpty() || prevLossesStr.isEmpty() || currentLossesStr.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            return
        }

        val investment = investmentStr.toDoubleOrNull() ?: 0.0
        val prevLosses = prevLossesStr.toDoubleOrNull() ?: 0.0
        val currentLosses = currentLossesStr.toDoubleOrNull() ?: 0.0

        if (investment == 0.0) {
            Toast.makeText(this, "O investimento não pode ser zero.", Toast.LENGTH_SHORT).show()
            return
        }

        // Lógica de Negócio (Business Value Blueprint)
        // Economia = O que se perdia antes - O que se perde agora
        val savings = prevLosses - currentLosses
        
        // ROI = (Lucro Líquido / Investimento) * 100
        // Lucro Líquido aqui é a Economia Gerada - Custo do Investimento
        val roi = ((savings - investment) / investment) * 100

        // Cálculo do ROI Ajustado por Eficiência
        // Fórmula: ROI * (Tempo Padrão / Tempo Real)
        // Se o tempo real for menor que o padrão (mais rápido), o ROI aumenta.
        val standardResponseTime = 15.0 // Meta padrão de 15 minutos (QRF)
        val realResponseTime = binding.sliderResponseTime.value.toDouble()
        
        val efficiencyFactor = standardResponseTime / realResponseTime
        val efficiencyRoi = roi * efficiencyFactor

        displayResult(roi, savings, efficiencyRoi)
    }

    private fun displayResult(roi: Double, savings: Double, efficiencyRoi: Double) {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        
        binding.tvRoiPercentage.text = String.format("%.1f%%", roi)
        binding.tvSavings.text = "Economia Gerada: ${currencyFormat.format(savings)}"

        // Feedback visual simples: Verde se positivo, Vermelho se negativo
        val colorRes = if (roi >= 0) android.R.color.holo_green_dark else android.R.color.holo_red_dark
        binding.tvRoiPercentage.setTextColor(resources.getColor(colorRes, theme))

        // Exibir ROI de Eficiência
        binding.tvRoiEfficiency.text = String.format("%.1f%%", efficiencyRoi)
        // Destaque visual se a eficiência for superior ao ROI base (tempo < 15 min)
        val effColor = if (efficiencyRoi >= roi) android.R.color.holo_green_dark else android.R.color.holo_orange_dark
        binding.tvRoiEfficiency.setTextColor(resources.getColor(effColor, theme))

        binding.cardResult.visibility = View.VISIBLE
    }
}