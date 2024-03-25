package com.serrano.dictproject.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import com.serrano.dictproject.activity.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferencesModule {

    @Provides
    @Singleton
    fun provideUserCacheDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.preferencesDataStore
    }

    @Provides
    @Singleton
    fun provideUserCacheRepository(preferencesDataStore: DataStore<Preferences>): PreferencesRepository {
        return PreferencesRepository(preferencesDataStore)
    }
}