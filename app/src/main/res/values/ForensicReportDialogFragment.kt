package com.example.project_gestoderisco.ui.gamification

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.project_gestoderisco.data.model.ForensicReport
import com.example.project_gestoderisco.databinding.DialogForensicReportBinding
import java.text.NumberFormat
import java.util.Locale

class ForensicReportDialogFragment : DialogFragment() {

    private var _binding: DialogForensicReportBinding? = null
    private val binding get() = _binding!!
    private var onDismissCallback: (() -> Unit)? = null

    companion object {
        private const val ARG_REPORT = "arg_report"

        fun newInstance(report: ForensicReport): ForensicReportDialogFragment {
            val fragment = ForensicReportDialogFragment()
            val args = Bundle()
            args.putSerializable(ARG_REPORT, report)
            fragment.arguments = args
            return fragment
        }
    }

    fun setOnDismissListener(listener: () -> Unit) {
        onDismissCallback = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogForensicReportBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val report = arguments?.getSerializable(ARG_REPORT) as? ForensicReport ?: return

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        binding.tvTitle.text = "ANÁLISE FORENSE: ${report.eventName.uppercase()}"
        binding.tvReactionTime.text = "${report.responseTimeSeconds}s"
        binding.tvGrade.text = report.disciplineGrade
        binding.tvGrade.setTextColor(if (report.disciplineGrade == "S") Color.GREEN else Color.YELLOW)
        
        binding.tvSavedCapital.text = currencyFormat.format(report.savedCapital)
        binding.tvPotentialLoss.text = "Prejuízo evitado de ${currencyFormat.format(report.potentialLoss)}"
        binding.tvInsight.text = report.insight

        binding.btnCollect.setOnClickListener {
            onDismissCallback?.invoke()
            dismiss()
        }
    }
}