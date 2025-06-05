package com.servicein.domain.model

data class Customer(
    val id: String = "",
    val customerName: String = "",
    val wallet: Int = 0,
) {
    constructor() : this("", "", 0)
}