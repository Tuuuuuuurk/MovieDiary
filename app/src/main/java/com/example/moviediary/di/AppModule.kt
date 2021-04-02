package com.example.moviediary.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.moviediary.data.Converters
import com.example.moviediary.data.MovieDiaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application, callback: MovieDiaryDatabase.Callback)
        = Room.databaseBuilder(app, MovieDiaryDatabase::class.java, "movie_diary_database")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Provides
    fun provideFilmDao(db: MovieDiaryDatabase) = db.filmDao()

    @Provides
    fun provideProducerDao(db: MovieDiaryDatabase) = db.producerDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    fun provideAppContext(app: Application): Context = app.baseContext
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope