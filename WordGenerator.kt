package com.example.gestaoderisco.utils

import android.content.Context
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Ocorrencia
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.OutputStream
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WordGenerator(private val context: Context) {

    fun generateWord(outputStream: OutputStream, dataList: List<Ocorrencia>) {
        val document = XWPFDocument()

        // Configuração de Margens ABNT (3cm sup/esq, 2cm inf/dir)
        // 1 cm ≈ 567 twips
        // Esquerda/Superior: 3cm * 567 = 1701
        // Direita/Inferior: 2cm * 567 = 1134
        val sectPr = document.document.body.addNewSectPr()
        val pageMar = sectPr.addNewPgMar()
        pageMar.left = BigInteger.valueOf(1701)
        pageMar.top = BigInteger.valueOf(1701)
        pageMar.right = BigInteger.valueOf(1134)
        pageMar.bottom = BigInteger.valueOf(1134)

        // Título da Instituição
        val titleParagraph = document.createParagraph()
        titleParagraph.alignment = ParagraphAlignment.CENTER
        val titleRun = titleParagraph.createRun()
        titleRun.isBold = true
        titleRun.fontSize = 12
        titleRun.fontFamily = "Times New Roman"
        titleRun.setText(context.getString(R.string.report_institution_name))
        titleRun.addBreak()
        titleRun.setText(context.getString(R.string.report_title))
        titleRun.addBreak()
        titleRun.addBreak()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))

        for (item in dataList) {
            // Título do Item (Loja - Categoria)
            val itemTitlePara = document.createParagraph()
            val itemTitleRun = itemTitlePara.createRun()
            itemTitleRun.isBold = true
            itemTitleRun.fontSize = 12
            itemTitleRun.fontFamily = "Times New Roman"
            itemTitleRun.setText("LOJA: ${item.loja} - ${item.categoriaProduto}")

            // Corpo do Item
            val itemBodyPara = document.createParagraph()
            val itemBodyRun = itemBodyPara.createRun()
            itemBodyRun.fontSize = 12
            itemBodyRun.fontFamily = "Times New Roman"
            
            // Data
            itemBodyRun.setText("Data: ${dateFormat.format(Date(item.dataRegistro))}")
            itemBodyRun.addBreak()
            
            // Valor
            itemBodyRun.setText("Valor Estimado: R$ ${String.format("%.2f", item.valorEstimado)}")
            itemBodyRun.addBreak()
            
            // Descrição
            itemBodyRun.setText("Descrição: ${item.relato}")
            
            // Separador visual (Linha)
            val separatorPara = document.createParagraph()
            separatorPara.borderBottom = org.apache.poi.xwpf.usermodel.Borders.SINGLE
        }

        document.write(outputStream)
        document.close()
    }
}