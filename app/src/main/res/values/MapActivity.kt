package com.example.gestaoderisco.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.gestaoderisco.R
import com.example.gestaoderisco.analysis.StatisticsAnalyzer
import com.example.gestaoderisco.databinding.ActivityMapBinding
import com.example.gestaoderisco.viewmodel.MapUiState
import com.example.gestaoderisco.viewmodel.MapViewModel
import com.example.gestaoderisco.viewmodel.MapViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapBinding
    private var map: GoogleMap? = null
    private val viewModel: MapViewModel by viewModels { MapViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupToolbar() {
        binding.toolbarMap.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                if (!state.isLoading && state.lojas.isNotEmpty() && map != null) {
                    addHeatMap(state)
                }
            }
        }
    }

    private fun addHeatMap(state: MapUiState) {
        val analyzer = StatisticsAnalyzer(state.ocorrencias)
        val ocorrenciasPorLoja = analyzer.getStatisticsByStore()

        val heatMapData = mutableListOf<WeightedLatLng>()
        val boundsBuilder = LatLngBounds.Builder()

        state.lojas.forEach { loja ->
            val count = ocorrenciasPorLoja[loja.nome] ?: 0
            if (count > 0 && loja.latitude != 0.0 && loja.longitude != 0.0) {
                val latLng = LatLng(loja.latitude, loja.longitude)
                // A intensidade do heatmap será o número de ocorrências
                heatMapData.add(WeightedLatLng(latLng, count.toDouble()))
                boundsBuilder.include(latLng)
            }
        }

        map?.clear() // Limpa o mapa antes de adicionar a nova camada

        if (heatMapData.isEmpty()) {
            // Se não houver dados, centraliza no Brasil, por exemplo
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-14.235, -51.925), 3f))
            return
        }

        val provider = HeatmapTileProvider.Builder()
            .weightedData(heatMapData)
            .radius(50) // Raio de influência de cada ponto em pixels
            .build()

        map?.addTileOverlay(TileOverlayOptions().tileProvider(provider))

        val bounds = boundsBuilder.build()
        // Adiciona um padding para que os pontos não fiquem nas bordas
        map?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150))
    }
}