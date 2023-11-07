package com.hmurai.telegrambot.repository

import com.hmurai.telegrambot.dto.UserDTO
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserRepository(
   private val mongoTemplate: MongoTemplate
) {
    @Value("\${mongo.collections.bot-users:bot-users}")
    lateinit var botUsersCollection: String

    fun getUserDTO(userId: String): UserDTO? {
        val query = Query().addCriteria(Criteria.where("userId").`is`(userId))
        return mongoTemplate.find(query, UserDTO::class.java, botUsersCollection).firstOrNull()
    }

    fun updateUserDTO(userDTO: UserDTO) {
        mongoTemplate.save(userDTO, botUsersCollection)
    }

}
