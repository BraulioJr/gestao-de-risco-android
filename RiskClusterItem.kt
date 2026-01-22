package com.example.gestaoderisco

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class RiskClusterItem(
    private val position: LatLng,
    private val title: String,
    private val snippet: String,
    val value: Double
) : ClusterItem {

    override fun getPosition(): LatLng = position
    override fun getTitle(): String = title
    override fun getSnippet(): String = snippet
    override fun getZIndex(): Float? = 0f
}