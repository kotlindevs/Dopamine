package com.google.android.piyush.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun dopamineDatabase(@ApplicationContext context: Context): DopamineDb {
        return Room.databaseBuilder(
            context = context,
            klass = DopamineDb::class.java,
            name = "dopamine_db"
        ).build()
    }

    @Provides
    @Singleton
    fun dopamineDao(database: DopamineDb): DopamineDao {
        return database.dopamineDao()
    }
}