package com.example.project_gestoderisco

@Database(entities = [Ocorrencia::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
	abstract fun ocorrenciaDao(): OcorrenciaDao

	companion object {
		@Volatile
		private var INSTANCE: AppDatabase? = null

		fun getDatabase(context: Context): AppDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"gestao_risco_db"
				).build()
				INSTANCE = instance
				instance
			}
		}
	}
}