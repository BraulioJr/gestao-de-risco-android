package com.example.project_gestoderisco.view

import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.project_gestoderisco.R
import com.example.project_gestoderisco.gamification.XpCalculationResult

class XpResultDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_RESULT = "arg_xp_result"

        fun newInstance(result: XpCalculationResult): XpResultDialogFragment {
            val fragment = XpResultDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_RESULT, result)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_xp_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val result = arguments?.getSerializable(ARG_RESULT) as? XpCalculationResult ?: return
        
        val tvXpGained = view.findViewById<TextView>(R.id.tvXpGained)
        val layoutBreakdown = view.findViewById<LinearLayout>(R.id.layoutBreakdown)
        val btnCollect = view.findViewById<Button>(R.id.btnCollect)

        // Efeito sonoro de missão cumprida
        playMissionAccomplishedSound()

        // Animação de contagem do XP
        animateXpCount(tvXpGained, result.finalXp)

        // Preencher Breakdown
        addBreakdownItem(layoutBreakdown, "Base da Missão", "+${result.basePoints}")
        if (result.missionPoints > 0) {
            addBreakdownItem(layoutBreakdown, "Objetivos Secundários", "+${result.missionPoints}")
        }
        if (result.streakBonus > 0) {
            addBreakdownItem(layoutBreakdown, "Bônus de Sequência", "+${result.streakBonus}", "#FFD700")
        }
        result.appliedMultipliers.forEach { (name, value) ->
            addBreakdownItem(layoutBreakdown, name, "x$value", "#4CAF50")
        }

        btnCollect.setOnClickListener {
            dismiss()
        }
    }

    private fun playMissionAccomplishedSound() {
        try {
            val toneGen = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            toneGen.startTone(ToneGenerator.TONE_PROP_ACK, 600)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun animateXpCount(textView: TextView, finalValue: Int) {
        val animator = ValueAnimator.ofInt(0, finalValue)
        animator.duration = 1000 // 1 segundo
        animator.addUpdateListener { animation ->
            textView.text = "+${animation.animatedValue} XP"
        }
        animator.start()
    }

    private fun addBreakdownItem(container: LinearLayout, label: String, value: String, colorHex: String = "#FFFFFF") {
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            setPadding(0, 4, 0, 4)
        }

        val tvLabel = TextView(context).apply { text = label; setTextColor(Color.LTGRAY); layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f) }
        val tvValue = TextView(context).apply { text = value; setTextColor(Color.parseColor(colorHex)); setTypeface(null, android.graphics.Typeface.BOLD) }

        row.addView(tvLabel)
        row.addView(tvValue)
        container.addView(row)
    }
}