package com.example.project_gestoderisco

@Dao
interface OcorrenciaDao {
	@Insert
	suspend fun insert(ocorrencia: Ocorrencia)

	@Query("SELECT * FROM ocorrencias ORDER BY dataRegistro DESC")
	suspend fun getAll(): List<Ocorrencia>
}