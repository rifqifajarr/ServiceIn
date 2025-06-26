package com.servicein.domain.usecase

import com.servicein.domain.repository.ICustomerRepository
import javax.inject.Inject

class CreateCustomerUseCase @Inject constructor(
    private val customerRepository: ICustomerRepository
) {
    suspend operator fun invoke(customerId: String, customerName: String): Result<Unit> {
        if (customerId.isBlank()) {
            return Result.failure(IllegalArgumentException("Customer ID tidak boleh kosong."))
        }
        if (customerName.isBlank()) {return Result.failure(IllegalArgumentException("Nama pelanggan tidak boleh kosong."))
        }
        return customerRepository.createCustomer(customerId, customerName)
    }
}