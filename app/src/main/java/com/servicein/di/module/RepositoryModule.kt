package com.servicein.di.module

import com.google.firebase.firestore.FirebaseFirestore
import com.servicein.data.repository.ChatRepository
import com.servicein.data.repository.CustomerRepository
import com.servicein.data.repository.OrderRepository
import com.servicein.data.repository.ShopRepository
import com.servicein.domain.repository.IChatRepository
import com.servicein.domain.repository.ICustomerRepository
import com.servicein.domain.repository.IOrderRepository
import com.servicein.domain.repository.IShopRepository
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

    @Provides
    fun provideShopRepository(firestore: FirebaseFirestore): IShopRepository {
        return ShopRepository(firestore)
    }

    @Provides
    fun provideCustomerRepository(firestore: FirebaseFirestore): ICustomerRepository {
        return CustomerRepository(firestore)
    }

    @Provides
    fun provideChatRepository(firestore: FirebaseFirestore): IChatRepository {
        return ChatRepository(firestore)
    }
}