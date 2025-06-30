package com.google.android.piyush.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.piyush.database.entities.RecentSearch
import com.google.android.piyush.database.entities.RecentlyExplored
import com.google.android.piyush.database.entities.User
import com.google.android.piyush.database.entities.UserPlaylists

@Database(
    entities = [RecentSearch::class,
        User::class,
        RecentlyExplored::class,
        UserPlaylists::class],
    version = 1,
    exportSchema = false
)
abstract class DopamineDb : RoomDatabase(){

    abstract fun dopamineDao() : DopamineDao

    companion object{

        @Volatile
        private var INSTANCE : DopamineDb? = null
        fun getDatabase(context: Context) : DopamineDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, DopamineDb::class.java, "dopamine_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}