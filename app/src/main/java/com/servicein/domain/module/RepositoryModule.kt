package com.servicein.domain.module

import com.google.firebase.firestore.FirebaseFirestore
import com.servicein.data.repository.OrderRepository
import com.servicein.domain.repository.IOrderRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    fun provideOrderRepository(firestore: FirebaseFirestore): IOrderRepository {
        return OrderRepository(firestore)
    }
}