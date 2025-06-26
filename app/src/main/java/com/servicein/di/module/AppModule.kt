package com.servicein.di.module

import android.content.Context
import com.servicein.data.preference.AppPreferencesManager
import com.servicein.domain.preferences.IAppPreferencesManager
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
    fun provideAppPreferencesManager(@ApplicationContext context: Context): IAppPreferencesManager {
        return AppPreferencesManager(context)
    }
}