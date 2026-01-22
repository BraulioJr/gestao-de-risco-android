package com.example.gestaoderisco

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.NumberFormat
import java.util.Locale

class CustomMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {

    private val tvContent: TextView = findViewById(R.id.tvContent)
    private val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        e?.let {
            tvContent.text = format.format(it.y.toDouble())
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        // Centraliza o marcador horizontalmente e o coloca acima do ponto
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}