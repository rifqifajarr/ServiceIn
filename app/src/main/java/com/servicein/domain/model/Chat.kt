package com.servicein.domain.model

data class Chat(
    val id: String = "",
    val shopId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val shopName: String = "",
    val customerMessages: List<String> = emptyList(),
    val shopMessages: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", "", emptyList(), emptyList(), 0)
}