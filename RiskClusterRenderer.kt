package com.example.gestaoderisco

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class RiskClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<RiskClusterItem>
) : DefaultClusterRenderer<RiskClusterItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: RiskClusterItem, markerOptions: MarkerOptions) {
        val hue = when {
            item.value >= 1000 -> BitmapDescriptorFactory.HUE_RED    // Alto Risco
            item.value >= 300 -> BitmapDescriptorFactory.HUE_ORANGE  // Médio Risco
            else -> BitmapDescriptorFactory.HUE_GREEN                // Baixo Risco
        }
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hue))
        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}