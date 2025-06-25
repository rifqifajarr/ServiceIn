package com.servicein.domain.model

data class Chat(
    val id: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val customerId: String = "",
    val customerName: String = "",
    val shopId: String = "",
    val shopName: String = "",
    val messages: List<Message> = emptyList(),
) {
    constructor() : this("", 0, "", "", "", "", emptyList())
}

data class Message(
    val text: String = "",
    val senderType: String = "", // "customer" atau "shop"
    val timestamp: Long = System.currentTimeMillis(),
)