package com.servicein.di.module

import android.content.Context
import com.servicein.data.notification.NotificationService
import com.servicein.data.notification.OrderStatusChangeDetector
import com.servicein.domain.notification.INotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {
    @Provides
    @Singleton
    fun provideNotificationService(
        @ApplicationContext context: Context
    ): INotificationService {
        return NotificationService(context)
    }

    @Provides
    @Singleton
    fun provideOrderStatusChangeDetector(
        notificationService: NotificationService,
        @ApplicationContext context: Context
    ): OrderStatusChangeDetector {
        return OrderStatusChangeDetector(notificationService, context)
    }
}