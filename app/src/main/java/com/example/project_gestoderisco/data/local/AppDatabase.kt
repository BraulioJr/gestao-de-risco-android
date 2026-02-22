package com.example.project_gestoderisco.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.project_gestoderisco.model.Risk

@Database(entities = [Risk::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun riskDao(): RiskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "risk-db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}