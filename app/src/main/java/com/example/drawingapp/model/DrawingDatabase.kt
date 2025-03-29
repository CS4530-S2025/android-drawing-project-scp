package com.example.drawingapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * This abstract class defines the Room database for storing drawing metadata.
 */
@Database(entities = [DrawingEntity::class], version = 1)
abstract class DrawingDatabase : RoomDatabase() {

    // Expose the DAO
    abstract fun drawingDao(): DrawingDao

    companion object {
        @Volatile
        private var INSTANCE: DrawingDatabase? = null

        /**
         * Singleton instance to avoid creating multiple DB connections.
         */
        fun getDatabase(context: Context): DrawingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrawingDatabase::class.java,
                    "drawing_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
