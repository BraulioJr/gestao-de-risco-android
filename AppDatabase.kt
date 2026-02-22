package com.example.gestaoderisco.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gestaoderisco.models.Ocorrencia
import com.example.gestaoderisco.models.ReconLog
import com.example.gestaoderisco.data.local.OcorrenciaDao
import com.example.gestaoderisco.data.local.ReconLogDao

@Database(entities = [Ocorrencia::class, ReconLog::class], version = 5)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ocorrenciaDao(): OcorrenciaDao
    abstract fun reconLogDao(): ReconLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val currentTime = System.currentTimeMillis()
                database.execSQL("ALTER TABLE ocorrencias ADD COLUMN dataRegistro INTEGER NOT NULL DEFAULT $currentTime")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ocorrencias ADD COLUMN perfilFurtante TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE ocorrencias ADD COLUMN latitude REAL NOT NULL DEFAULT 0.0")
                database.execSQL("ALTER TABLE ocorrencias ADD COLUMN longitude REAL NOT NULL DEFAULT 0.0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ocorrencias ADD COLUMN tenantId TEXT NOT NULL DEFAULT 'default_tenant'")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `recon_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `message` TEXT NOT NULL, `type` TEXT NOT NULL, `attachmentPath` TEXT)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "gestao_risco_db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}