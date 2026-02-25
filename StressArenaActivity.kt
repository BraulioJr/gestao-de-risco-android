package com.example.project_gestoderisco.ui.gamification

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.project_gestoderisco.databinding.ActivityStressArenaBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.project_gestoderisco.utils.SoundManager

@AndroidEntryPoint
class StressArenaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStressArenaBinding
    private val viewModel: GamificationViewModel by viewModels()
    
    @Inject
    lateinit var soundManager: SoundManager
    
    private var alertAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStressArenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish() // Retorna ao fluxo principal (Dashboard)
        }

        binding.btnMitigate.setOnClickListener {
            viewModel.onMitigateAction()
        }

        // Debug button to trigger the event
        binding.btnSimulateCrash.setOnClickListener {
            viewModel.triggerMarketCrash()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.userStats.collectLatest { stats ->
                binding.tvLevel.text = "Nível: ${stats.level} (XP: ${stats.xp})"
                binding.tvCrashes.text = "Crashes: ${stats.survivedCrashes}"
            }
        }

        lifecycleScope.launch {
            viewModel.isStressActive.collectLatest { isActive ->
                updateStressUI(isActive)
            }
        }

        lifecycleScope.launch {
            viewModel.forensicReport.collectLatest { report ->
                report?.let {
                    playSoundForGrade(it.disciplineGrade)
                    val dialog = ForensicReportDialogFragment.newInstance(it)
                    dialog.setOnDismissListener { viewModel.dismissReport() }
                    dialog.show(supportFragmentManager, "ForensicReport")
                }
            }
        }
    }

    private fun playSoundForGrade(grade: String) {
        when (grade) {
            "S" -> soundManager.play(SoundManager.GameSound.RANK_S)
            "D" -> soundManager.play(SoundManager.GameSound.RANK_D)
            else -> soundManager.play(SoundManager.GameSound.RANK_B)
        }
    }

    private fun updateStressUI(isStressActive: Boolean) {
        if (isStressActive) {
            binding.tvScenarioStatus.text = "CENÁRIO: VOLATILIDADE EXTREMA!"
            binding.tvScenarioStatus.setTextColor(Color.RED)
            binding.btnMitigate.setBackgroundColor(Color.RED)
            binding.btnMitigate.isEnabled = true
            startAlertAnimation()
        } else {
            binding.tvScenarioStatus.text = "CENÁRIO: ESTÁVEL"
            binding.tvScenarioStatus.setTextColor(Color.GRAY)
            binding.btnMitigate.setBackgroundColor(Color.DKGRAY)
            stopAlertAnimation()
        }
    }

    private fun startAlertAnimation() {
        val colorFrom = Color.parseColor("#0D1117")
        val colorTo = Color.parseColor("#440000")
        alertAnimator = ObjectAnimator.ofObject(binding.rootLayout, "backgroundColor", ArgbEvaluator(), colorFrom, colorTo).apply {
            duration = 500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }
    }

    private fun stopAlertAnimation() {
        alertAnimator?.cancel()
        binding.rootLayout.setBackgroundColor(Color.parseColor("#0D1117"))
    }
}