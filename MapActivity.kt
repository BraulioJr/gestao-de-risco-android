package com.example.gestaoderisco

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.gestaoderisco.data.AppDatabase
import com.example.gestaoderisco.models.Ocorrencia
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.TileOverlay
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.maps.android.SphericalUtil
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.heatmaps.HeatmapTileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import com.example.gestaoderisco.utils.AbntPdfGenerator // Importar a classe criada
import com.example.gestaoderisco.utils.CsvGenerator
import com.example.gestaoderisco.utils.WordGenerator

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var clusterManager: ClusterManager<RiskClusterItem>
    private lateinit var spinnerFilter: Spinner
    private lateinit var searchView: SearchView
    private lateinit var switchHeatmap: SwitchMaterial
    private lateinit var fabMyLocation: FloatingActionButton
    private lateinit var fabExportMenu: FloatingActionButton
    private lateinit var fabExportPdf: FloatingActionButton
    private lateinit var fabExportCsv: FloatingActionButton
    private lateinit var fabExportWord: FloatingActionButton
    private var isFabMenuOpen = false
    
    private var currentRiskCircle: Circle? = null
    private var heatmapOverlay: TileOverlay? = null
    private var currentOcorrencias: List<Ocorrencia> = emptyList()
    
    // SaaS: Propriedade dinâmica para suportar Multi-tenancy
    private val currentTenantId: String
        get() = "c7b3851e-28ee-4262-bd6b-f917d5c47ec2" // TODO: Recuperar do Token de Autenticação/Sessão do Usuário (ex: Firebase Auth)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            enableMyLocation()
        } else {
            Toast.makeText(this, getString(R.string.location_permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para criar o arquivo PDF
    private val createPdfLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        uri?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    AbntPdfGenerator(applicationContext).generatePdf(outputStream, currentOcorrencias)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapActivity, getString(R.string.export_success), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Launcher para criar o arquivo CSV
    private val createCsvLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    CsvGenerator(applicationContext).generateCsv(outputStream, currentOcorrencias)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapActivity, getString(R.string.export_success), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Launcher para criar o arquivo Word (Doc)
    private val createWordLauncher = registerForActivityResult(
        ActivityResultContracts.CreateDocument("application/msword")
    ) { uri ->
        uri?.let {
            lifecycleScope.launch(Dispatchers.IO) {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    WordGenerator(applicationContext).generateWord(outputStream, currentOcorrencias)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MapActivity, getString(R.string.export_success), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        spinnerFilter = findViewById(R.id.spinnerMapFilter)
        searchView = findViewById(R.id.searchViewMap)
        switchHeatmap = findViewById(R.id.switchHeatmap)
        fabMyLocation = findViewById(R.id.fabMyLocation)
        fabExportMenu = findViewById(R.id.fabExportMenu)
        fabExportPdf = findViewById(R.id.fabExportPdf)
        fabExportCsv = findViewById(R.id.fabExportCsv)
        fabExportWord = findViewById(R.id.fabExportWord)

        setupSearch()
        setupFilterSpinner()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        switchHeatmap.setOnCheckedChangeListener { _, isChecked ->
            updateMapVisualization(isChecked)
        }

        fabMyLocation.setOnClickListener {
            checkLocationPermissionAndCenter()
        }

        setupFabMenu()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true

        // Configurações de interação
        mMap.setOnMapLongClickListener { latLng ->
            val intent = Intent(this, RegistroOcorrenciaActivity::class.java).apply {
                putExtra("LATITUDE", latLng.latitude)
                putExtra("LONGITUDE", latLng.longitude)
            }
            startActivity(intent)
        }

        mMap.setOnMapClickListener {
            currentRiskCircle?.remove()
            currentRiskCircle = null
        }

        setupClusterManager()
        
        // Carrega dados iniciais
        loadRiskPoints(spinnerFilter.selectedItemPosition, null)
    }

    private fun setupClusterManager() {
        clusterManager = ClusterManager(this, mMap)
        clusterManager.renderer = RiskClusterRenderer(this, mMap, clusterManager)
        
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)

        clusterManager.setOnClusterClickListener { cluster ->
            showClusterCoverage(cluster)
            true
        }
    }

    private fun checkLocationPermissionAndCenter() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                }
            }
        }
    }

    private fun setupFabMenu() {
        // Inicialmente esconde os botões de ação
        fabExportPdf.visibility = View.GONE
        fabExportCsv.visibility = View.GONE
        fabExportWord.visibility = View.GONE

        fabExportMenu.setOnClickListener { toggleFabMenu() }

        fabExportPdf.setOnClickListener {
            createPdfLauncher.launch("Relatorio_Riscos_ABNT.pdf")
            toggleFabMenu()
        }
        fabExportCsv.setOnClickListener {
            createCsvLauncher.launch("Relatorio_Riscos.csv")
            toggleFabMenu()
        }
        fabExportWord.setOnClickListener {
            createWordLauncher.launch("Relatorio_Riscos_ABNT.doc")
            toggleFabMenu()
        }
    }

    private fun toggleFabMenu() {
        isFabMenuOpen = !isFabMenuOpen
        val visibility = if (isFabMenuOpen) View.VISIBLE else View.GONE
        
        fabExportPdf.visibility = visibility
        fabExportCsv.visibility = visibility
        fabExportWord.visibility = visibility
        
        // Animação de rotação para indicar abertura/fechamento
        fabExportMenu.animate().rotation(if (isFabMenuOpen) 45f else 0f).start()
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                loadRiskPoints(spinnerFilter.selectedItemPosition, query)
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    loadRiskPoints(spinnerFilter.selectedItemPosition, null)
                }
                return false
            }
        })
    }

    private fun setupFilterSpinner() {
        val options = listOf("Todos", "Últimos 7 dias", "Últimos 30 dias", "Este Ano")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilter.adapter = adapter

        spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (::mMap.isInitialized) {
                    loadRiskPoints(position, searchView.query.toString())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadRiskPoints(filterOption: Int, searchQuery: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(applicationContext)
            val (startDate, endDate) = getFilterDates(filterOption)
            
            val allOcorrencias = if (startDate != null && endDate != null) {
                db.ocorrenciaDao().getByDateRange(currentTenantId, startDate, endDate)
            } else {
                db.ocorrenciaDao().getAll(currentTenantId)
            }

            var filteredList = allOcorrencias
            var targetLocation: LatLng? = null

            if (!searchQuery.isNullOrEmpty()) {
                val byStore = allOcorrencias.filter { it.loja.contains(searchQuery, ignoreCase = true) }
                if (byStore.isNotEmpty()) {
                    filteredList = byStore
                } else {
                    try {
                        val geocoder = Geocoder(applicationContext)
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocationName(searchQuery, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val address = addresses[0]
                            targetLocation = LatLng(address.latitude, address.longitude)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            withContext(Dispatchers.Main) {
                currentOcorrencias = filteredList
                updateMapVisualization(switchHeatmap.isChecked)

                if (targetLocation != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetLocation, 15f))
                } else if (filteredList.isNotEmpty()) {
                    val boundsBuilder = LatLngBounds.Builder()
                    var hasPoints = false
                    for (item in filteredList) {
                        if (item.latitude != 0.0 && item.longitude != 0.0) {
                            boundsBuilder.include(LatLng(item.latitude, item.longitude))
                            hasPoints = true
                        }
                    }
                    if (hasPoints) {
                        try {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 150))
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                }
            }
        }
    }

    private fun updateMapVisualization(showHeatmap: Boolean) {
        clusterManager.clearItems()
        heatmapOverlay?.remove()
        currentRiskCircle?.remove()

        val validPoints = currentOcorrencias.filter { it.latitude != 0.0 && it.longitude != 0.0 }

        if (showHeatmap) {
            if (validPoints.isNotEmpty()) {
                val latLngs = validPoints.map { LatLng(it.latitude, it.longitude) }
                val provider = HeatmapTileProvider.Builder()
                    .data(latLngs)
                    .radius(50)
                    .build()
                heatmapOverlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(provider))
            }
        } else {
            for (item in validPoints) {
                val location = LatLng(item.latitude, item.longitude)
                val clusterItem = RiskClusterItem(
                    location,
                    item.loja,
                    "${item.categoriaProduto} - R$ ${item.valorEstimado}",
                    item.valorEstimado
                )
                clusterManager.addItem(clusterItem)
            }
            clusterManager.cluster()
        }
    }

    private fun showClusterCoverage(cluster: Cluster<RiskClusterItem>) {
        currentRiskCircle?.remove()
        val center = cluster.position
        var maxDistance = 0.0
        for (item in cluster.items) {
            val dist = SphericalUtil.computeDistanceBetween(center, item.position)
            if (dist > maxDistance) maxDistance = dist
        }
        if (maxDistance < 100) maxDistance = 100.0
        currentRiskCircle = mMap.addCircle(
            CircleOptions().center(center).radius(maxDistance)
                .strokeColor(Color.RED).fillColor(Color.parseColor("#40FF0000")).strokeWidth(3f)
        )
    }

    private fun getFilterDates(option: Int): Pair<Long?, Long?> {
        val cal = Calendar.getInstance()
        val now = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0)
        return when (option) {
            1 -> { cal.add(Calendar.DAY_OF_YEAR, -7); Pair(cal.timeInMillis, now) }
            2 -> { cal.add(Calendar.DAY_OF_YEAR, -30); Pair(cal.timeInMillis, now) }
            3 -> { cal.set(Calendar.DAY_OF_YEAR, 1); Pair(cal.timeInMillis, now) }
            else -> Pair(null, null)
        }
    }
}