package com.hmurai.telegrambot.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class BotConfig {
    @Value("\${bot.token}")
    var token: String? = null

    @Value("\${bot.username}")
    var botName: String? = null
}
