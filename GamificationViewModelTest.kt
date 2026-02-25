package com.example.project_gestoderisco.ui.gamification

import com.example.project_gestoderisco.data.model.UserStats
import com.example.project_gestoderisco.data.repository.GamificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class GamificationViewModelTest {

    private lateinit var viewModel: GamificationViewModel
    private lateinit var repository: GamificationRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = Mockito.mock(GamificationRepository::class.java)
        
        // Mock inicial do repositório
        `when`(repository.getUserStats()).thenReturn(UserStats())
        
        viewModel = GamificationViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `calculate savedCapital correctly for fast reaction (2s)`() {
        // CENÁRIO: Flash Crash (Severidade 0.8 -> Max Loss 40%)
        // Posição: 10.000 -> Perda Potencial: 4.000
        // Reação: 2 segundos -> Fator Mitigação: (10 - 2) / 10 = 0.8
        // Capital Salvo Esperado: 4.000 * 0.8 = 3.200

        var fakeTime = 100000L
        viewModel.timeProvider = { fakeTime }

        viewModel.triggerMarketCrash()
        
        // Avança 2 segundos
        fakeTime += 2000 
        viewModel.onMitigateAction()

        val report = viewModel.forensicReport.value
        assertEquals(3200.0, report?.savedCapital ?: 0.0, 0.01)
        assertEquals("S", report?.disciplineGrade)
    }

    @Test
    fun `calculate savedCapital correctly for medium reaction (5s)`() {
        // Reação: 5 segundos -> Fator Mitigação: (10 - 5) / 10 = 0.5
        // Capital Salvo Esperado: 4.000 * 0.5 = 2.000

        var fakeTime = 100000L
        viewModel.timeProvider = { fakeTime }

        viewModel.triggerMarketCrash()
        
        // Avança 5 segundos
        fakeTime += 5000
        viewModel.onMitigateAction()

        val report = viewModel.forensicReport.value
        assertEquals(2000.0, report?.savedCapital ?: 0.0, 0.01)
        assertEquals("B", report?.disciplineGrade)
    }

    @Test
    fun `calculate savedCapital correctly for slow reaction (10s)`() {
        // Reação: 10 segundos -> Fator Mitigação: (10 - 10) / 10 = 0.0
        // Capital Salvo Esperado: 0.0

        var fakeTime = 100000L
        viewModel.timeProvider = { fakeTime }

        viewModel.triggerMarketCrash()
        
        // Avança 10 segundos
        fakeTime += 10000
        viewModel.onMitigateAction()

        val report = viewModel.forensicReport.value
        assertEquals(0.0, report?.savedCapital ?: 0.0, 0.01)
        assertEquals("D", report?.disciplineGrade)
    }
}