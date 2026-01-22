package com.example.gestaoderisco.models

// Modelo para representar uma loja, incluindo suas coordenadas geográficas.
data class Loja(
    val numero: String = "",
    val nome: String = "",
    val endereco: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)