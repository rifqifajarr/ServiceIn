package com.servicein.domain.usecase

import com.servicein.domain.model.Customer
import com.servicein.domain.repository.ICustomerRepository
import javax.inject.Inject

class GetCustomerUseCase @Inject constructor(
    private val customerRepository: ICustomerRepository
) {suspend operator fun invoke(customerId: String): Result<Customer?> {
        if (customerId.isBlank()) {
            return Result.failure(IllegalArgumentException("Customer ID tidak boleh kosong."))
        }
        return customerRepository.getCustomerById(customerId)
    }
}