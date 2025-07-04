package com.google.android.piyush.youtube.utilities

import com.google.android.piyush.youtube.repository.YoutubeRepository
import com.google.android.piyush.youtube.repository.YoutubeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.InternalSerializationApi
import javax.inject.Singleton

@Module
@InternalSerializationApi
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun youtubeRepository(
        repositoryImpl: YoutubeRepositoryImpl
    ) : YoutubeRepository
}