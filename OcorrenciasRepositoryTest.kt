package com.example.gestaoderisco.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gestaoderisco.data.AppDatabase
import com.example.gestaoderisco.data.local.OcorrenciaDao
import com.example.gestaoderisco.models.Ocorrencia
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class OcorrenciasRepositoryTest {

    private lateinit var ocorrenciaDao: OcorrenciaDao
    private lateinit var db: AppDatabase
    private lateinit var repository: OcorrenciasRepository

    private val tenantIdPrincipal = "c7b3851e-28ee-4262-bd6b-f917d5c47ec2"

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Usamos um banco de dados em memória para que ele seja limpo após o teste
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Permitido em testes para simplificar
            .build()
        ocorrenciaDao = db.ocorrenciaDao()
        repository = OcorrenciasRepository(context)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun salvarOcorrencia_deveInserirNoBancoComStatusNaoSincronizado() = runTest {
        // 1. Prepara
        val novaOcorrencia = Ocorrencia(
            loja = "Loja Teste",
            categoriaProduto = "Eletrônicos",
            valorEstimado = 500.0,
            acaoRealizada = "Monitoramento",
            relato = "Teste de inserção",
            tenantId = tenantIdPrincipal
        )

        // 2. Executa
        repository.salvarOcorrencia(novaOcorrencia)

        // 3. Verifica
        val ocorrenciasSalvas = ocorrenciaDao.getOcorrencias(tenantIdPrincipal, "").first()
        val ocorrenciaSalva = ocorrenciasSalvas.first()

        assertNotNull(ocorrenciaSalva)
        assertEquals("Loja Teste", ocorrenciaSalva.loja)
        assertFalse("A ocorrência salva deve ser marcada como não sincronizada", ocorrenciaSalva.isSynced)
    }

    @Test
    @Throws(Exception::class)
    fun getOcorrencias_deveRetornarApenasOcorrenciasDoTenantCorreto() = runTest {
        // 1. Prepara
        val ocorrenciaTenant1 = Ocorrencia(loja = "Loja A", categoriaProduto = "Cat A", valorEstimado = 10.0, acaoRealizada = "A", relato = "R", tenantId = tenantIdPrincipal)
        val ocorrenciaTenant2 = Ocorrencia(loja = "Loja B", categoriaProduto = "Cat B", valorEstimado = 20.0, acaoRealizada = "B", relato = "R", tenantId = "outro-tenant-id")
        
        ocorrenciaDao.insert(ocorrenciaTenant1)
        ocorrenciaDao.insert(ocorrenciaTenant2)

        // 2. Executa
        val resultadoFlow = repository.getOcorrencias("")
        val listaResultado = resultadoFlow.first()

        // 3. Verifica
        assertEquals("A lista deve conter apenas 1 ocorrência para o tenant principal", 1, listaResultado.size)
        assertEquals("A ocorrência retornada deve ser a do tenant principal", tenantIdPrincipal, listaResultado[0].tenantId)
    }
}