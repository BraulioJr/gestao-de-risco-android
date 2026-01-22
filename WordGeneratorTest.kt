package com.example.gestaoderisco.utils

import android.content.Context
import com.example.gestaoderisco.R
import com.example.gestaoderisco.models.Ocorrencia
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.ByteArrayOutputStream

class WordGeneratorTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var wordGenerator: WordGenerator

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        // Mock dos recursos de string usados no gerador
        `when`(mockContext.getString(R.string.report_institution_name)).thenReturn("INSTITUIÇÃO TESTE")
        `when`(mockContext.getString(R.string.report_title)).thenReturn("RELATÓRIO TESTE")
        
        wordGenerator = WordGenerator(mockContext)
    }

    @Test
    fun `generateWord deve conter margens ABNT corretas no CSS`() {
        val outputStream = ByteArrayOutputStream()
        val dummyList = emptyList<Ocorrencia>()

        wordGenerator.generateWord(outputStream, dummyList)

        val content = outputStream.toString("UTF-8")
        
        // Verifica a presença da regra CSS de margem ABNT
        // Margens: Superior/Esquerda = 3cm, Inferior/Direita = 2cm
        // CSS esperado: margin: 3.0cm 2.0cm 2.0cm 3.0cm;
        assertTrue("O HTML gerado deve conter as margens ABNT (3cm sup/esq, 2cm inf/dir)", 
            content.contains("margin: 3.0cm 2.0cm 2.0cm 3.0cm;"))
    }
}