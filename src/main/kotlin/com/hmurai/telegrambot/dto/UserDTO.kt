package com.hmurai.telegrambot.dto

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "bot-users")
data class UserDTO(
    @Id
    val userId: String,
    var firstName: String,
    val lastName: String,
    val selectedNewsCategories: MutableSet<String> = mutableSetOf(),
    var lockedSelectedNewsCategories: MutableSet<String> = mutableSetOf(),
    val createdAt: Date,
    var updatedAt: Date
)
