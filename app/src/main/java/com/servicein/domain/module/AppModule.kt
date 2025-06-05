package com.servicein.domain.module

import android.content.Context
import com.servicein.domain.preference.AppPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideAppPreferencesManager(@ApplicationContext context: Context): AppPreferencesManager {
        return AppPreferencesManager(context)
    }
}