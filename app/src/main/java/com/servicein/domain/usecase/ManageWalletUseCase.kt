package com.servicein.domain.usecase

import com.servicein.data.repository.ShopRepository
import com.servicein.domain.repository.ICustomerRepository
import javax.inject.Inject

class ManageWalletUseCase @Inject constructor(
    private val customerRepository: ICustomerRepository,
    private val shopRepository: ShopRepository
) {
    suspend fun add(customerId: String, amount: Int): Result<Int> {
        if (customerId.isBlank()) {
            return Result.failure(IllegalArgumentException("Customer ID tidak boleh kosong."))
        }
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Jumlah penambahan harus positif."))
        }
        return customerRepository.addToWallet(customerId, amount)
    }

    suspend fun pay(shopId: String, amount: Int): Result<Unit> {
        if (shopId.isBlank()) {
            return Result.failure(IllegalArgumentException("Shop ID tidak boleh kosong."))
        }
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Jumlah pembayaran harus positif."))
        }
        return shopRepository.addToWallet(shopId, amount - 5000)
    }

    suspend fun deduct(customerId: String, amount: Int): Result<Int> {
        if (customerId.isBlank()) {
            return Result.failure(IllegalArgumentException("Customer ID tidak boleh kosong."))
        }
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("Jumlah pengurangan harus positif."))
        }
        return customerRepository.deductFromWallet(customerId, amount)
    }
}