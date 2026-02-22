package com.example.project_gestoderisco.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.project_gestoderisco.model.Risk
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RiskDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: RiskDao

    @Before
    fun setup() {
        // Cria um banco de dados em memória para testes (não persiste no disco)
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Permitido apenas em testes
            .build()
        dao = database.riskDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetUnsyncedRisks() = runBlocking {
        // CENÁRIO: Inserir 1 risco sincronizado e 1 não sincronizado
        val riskSynced = Risk(
            name = "Risco Sincronizado",
            description = "Já enviado",
            probability = 1, impact = 1, impactAnalysis = "", mitigationActions = "",
            isSynced = true
        )

        val riskUnsynced = Risk(
            name = "Risco Offline",
            description = "Ainda no dispositivo",
            probability = 3, impact = 3, impactAnalysis = "", mitigationActions = "",
            isSynced = false
        )

        dao.insert(riskSynced)
        dao.insert(riskUnsynced)

        // AÇÃO: Buscar riscos não sincronizados
        val unsyncedList = dao.getUnsyncedRisks()

        // VERIFICAÇÃO: Deve retornar apenas o risco offline
        assertEquals(1, unsyncedList.size)
        assertEquals("Risco Offline", unsyncedList[0].name)
    }
}