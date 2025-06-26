package com.servicein.domain.repository

import com.servicein.domain.model.Customer

interface ICustomerRepository {
    suspend fun createCustomer(customerId: String, customerName: String): Result<Unit>

    suspend fun getCustomerById(customerId: String): Result<Customer?>

    suspend fun addToWallet(customerId: String, amount: Int): Result<Int>

    suspend fun deductFromWallet(customerId: String, amount: Int): Result<Int>
}