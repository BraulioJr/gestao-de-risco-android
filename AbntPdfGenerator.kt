package com.example.gestaoderisco.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Ocorrencia
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AbntPdfGenerator(private val context: Context) {

    // Conversão: 1 cm ≈ 28.35 pontos (PostScript points)
    // ABNT: Margem Esq/Sup = 3cm, Dir/Inf = 2cm
    private val POINTS_PER_CM = 28.35f
    private val MARGIN_LEFT = (3 * POINTS_PER_CM).toInt()
    private val MARGIN_TOP = (3 * POINTS_PER_CM).toInt()
    private val MARGIN_RIGHT = (2 * POINTS_PER_CM).toInt()
    private val MARGIN_BOTTOM = (2 * POINTS_PER_CM).toInt()

    // Tamanho A4: 595 x 842 pontos
    private val PAGE_WIDTH = 595
    private val PAGE_HEIGHT = 842
    private val CONTENT_WIDTH = PAGE_WIDTH - MARGIN_LEFT - MARGIN_RIGHT

    fun generatePdf(outputStream: OutputStream, dataList: List<Ocorrencia>) {
        val document = PdfDocument()
        val paint = Paint()

        // --- 1. CAPA (Elemento Pré-Textual) ---
        val pageInfoCapa = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val pageCapa = document.startPage(pageInfoCapa)
        val canvasCapa = pageCapa.canvas

        drawCover(canvasCapa, paint)
        document.finishPage(pageCapa)

        // --- 2. CONTEÚDO (Elemento Textual) ---
        var pageNumber = 2
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas = page.canvas
        var currentY = MARGIN_TOP.toFloat()

        // Configuração da Fonte do Corpo (Arial/Times 12)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 12f
        paint.color = Color.BLACK

        // Título da Seção
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText("1. LISTAGEM DE OCORRÊNCIAS", MARGIN_LEFT.toFloat(), currentY, paint)
        currentY += 30
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (item in dataList) {
            // Verifica se cabe na página, senão cria nova
            if (currentY > PAGE_HEIGHT - MARGIN_BOTTOM - 100) {
                document.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber).create()
                page = document.startPage(pageInfo)
                canvas = page.canvas
                currentY = MARGIN_TOP.toFloat()
            }

            // Desenhando o item
            val title = "Loja: ${item.loja} - ${item.categoriaProduto}"
            val details = "Data: ${dateFormat.format(Date(item.dataRegistro))}\n" +
                          "Valor: R$ ${String.format("%.2f", item.valorEstimado)}\n" +
                          "Descrição: ${item.relato}"

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            currentY = drawMultilineText(canvas, title, MARGIN_LEFT.toFloat(), currentY, paint)
            
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            currentY = drawMultilineText(canvas, details, MARGIN_LEFT.toFloat(), currentY + 5, paint)
            
            currentY += 20 // Espaço entre itens
        }

        document.finishPage(page)

        // Salva e fecha
        try {
            document.writeTo(outputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            document.close()
        }
    }

    private fun drawCover(canvas: Canvas, paint: Paint) {
        val centerX = PAGE_WIDTH / 2f
        
        // Instituição (Topo, Centralizado, Negrito, Maiúsculas)
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 12f
        paint.textAlign = Paint.Align.CENTER
        
        var y = MARGIN_TOP.toFloat()
        canvas.drawText(context.getString(R.string.report_institution_name), centerX, y, paint)

        // Autor (Meio do topo)
        y += 150
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText("AUTOR DO RELATÓRIO (SISTEMA)", centerX, y, paint)

        // Título (Centro da página)
        y = PAGE_HEIGHT / 2f - 50
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 14f
        canvas.drawText(context.getString(R.string.report_title), centerX, y, paint)
        
        y += 20
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        canvas.drawText(context.getString(R.string.report_subtitle), centerX, y, paint)

        // Cidade e Ano (Rodapé)
        y = PAGE_HEIGHT - MARGIN_BOTTOM.toFloat()
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        canvas.drawText(context.getString(R.string.report_city_year), centerX, y, paint)
        
        // Reset Align
        paint.textAlign = Paint.Align.LEFT
    }

    // Função auxiliar para quebrar texto em múltiplas linhas
    private fun drawMultilineText(canvas: Canvas, text: String, x: Float, startY: Float, paint: Paint): Float {
        val lineHeight = paint.descent() - paint.ascent() + 2
        var y = startY

        val lines = text.split("\n")
        for (line in lines) {
            val words = line.split(" ")
            var currentLine = ""
            
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                val width = paint.measureText(testLine)
                
                if (width < CONTENT_WIDTH) {
                    currentLine = testLine
                } else {
                    canvas.drawText(currentLine, x, y, paint)
                    y += lineHeight
                    currentLine = word
                }
            }
            if (currentLine.isNotEmpty()) {
                canvas.drawText(currentLine, x, y, paint)
                y += lineHeight
            }
        }
        return y
    }
}