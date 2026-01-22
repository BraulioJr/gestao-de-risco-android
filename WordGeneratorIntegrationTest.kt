package com.example.gestaoderisco.utils

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.gestaoderisco.models.Ocorrencia
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class WordGeneratorIntegrationTest {

    private lateinit var context: Context
    private lateinit var wordGenerator: WordGenerator
    private lateinit var testFile: File

    @Before
    fun setUp() {
        // Obtém o contexto da aplicação instrumentada (o app real)
        context = InstrumentationRegistry.getInstrumentation().targetContext
        wordGenerator = WordGenerator(context)

        // Cria um arquivo temporário no diretório de cache do aplicativo para o teste
        // Isso simula o armazenamento sem poluir a galeria ou documentos do usuário
        val outputDir = context.cacheDir
        testFile = File.createTempFile("test_report_integration", ".doc", outputDir)
    }

    @After
    fun tearDown() {
        // Limpa o arquivo criado após o teste para não deixar resíduos
        if (testFile.exists()) {
            testFile.delete()
        }
    }

    @Test
    fun generateWord_savesFileToStorage_withCorrectContent() {
        // 1. Preparar dados de teste
        val ocorrenciaTeste = Ocorrencia(
            loja = "Loja Integração",
            categoriaProduto = "Teste Automatizado",
            valorEstimado = 150.00,
            acaoRealizada = "Monitoramento",
            relato = "Teste de integração do WordGenerator",
            dataRegistro = System.currentTimeMillis(),
            perfilFurtante = "Ocasional",
            latitude = 0.0,
            longitude = 0.0,
            tenantId = "test_tenant"
        )
        val dataList = listOf(ocorrenciaTeste)

        // 2. Executar a geração do arquivo
        // Simulamos o OutputStream escrevendo diretamente no arquivo temporário
        FileOutputStream(testFile).use { outputStream ->
            wordGenerator.generateWord(outputStream, dataList)
        }

        // 3. Verificações (Asserts)

        // Verifica se o arquivo existe fisicamente no disco
        assertTrue("O arquivo deve ter sido criado no armazenamento", testFile.exists())

        // Verifica se o arquivo não está vazio
        assertTrue("O arquivo deve ter conteúdo (tamanho > 0)", testFile.length() > 0)

        // Lê o conteúdo usando Apache POI para verificar se é um DOCX válido e contém os dados
        FileInputStream(testFile).use { inputStream ->
            val document = XWPFDocument(inputStream)
            val paragraphs = document.paragraphs
            val fullText = paragraphs.joinToString("\n") { it.text }

            assertTrue("Deve conter o nome da loja", fullText.contains("Loja Integração"))
            assertTrue("Deve conter a categoria", fullText.contains("Teste Automatizado"))
        }
    }
}