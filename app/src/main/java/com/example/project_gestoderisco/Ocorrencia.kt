package com.example.project_gestoderisco

@Entity(tableName = "ocorrencias")
data class Ocorrencia(
	@PrimaryKey(autoGenerate = true) val id: Long = 0,
	val loja: String,
	val categoriaProduto: String,
	val valorEstimado: Double,
	val acaoRealizada: String,
	val relato: String,
	val dataRegistro: Long = System.currentTimeMillis() // Salva a data/hora atual automaticamente
)