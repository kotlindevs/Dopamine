package com.google.android.piyush.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.piyush.database.dao.DopamineDao
import com.google.android.piyush.database.entities.EntityFavouritePlaylist
import com.google.android.piyush.database.entities.EntityRecentVideos
import com.google.android.piyush.database.entities.EntityVideoSearch

@Database(entities = [EntityVideoSearch::class, EntityRecentVideos::class,EntityFavouritePlaylist::class], version = 1, exportSchema = false)
abstract class DopamineDatabase : RoomDatabase() {
    abstract fun dopamineDao(): DopamineDao
    companion object {
        @Volatile
        private var INSTANCE: DopamineDatabase? = null
        private const val DATABASE_NAME = "dopamine_database"

        fun getDatabase(context: Context): DopamineDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DopamineDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}