package com.servicein.domain.usecase

import com.servicein.domain.repository.ICustomerRepository
import javax.inject.Inject

class ManageWalletUseCase @Inject constructor(
    private val customerRepository: ICustomerRepository
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