package com.hmurai.telegrambot.dto

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand

interface NewsCommands {
    companion object {
        val LIST_OF_COMMANDS = listOf(
            BotCommand("/start", "start bot"),
            BotCommand("/help", "bot info"),
            BotCommand("/breaking", "breaking news"),
            BotCommand("/customize", "customized news")
        )
        const val HELP_TEXT = "This bot will help to count the number of messages in the chat. " +
                "The following commands are available to you:\n\n" +
                "/start - start the bot\n" +
                "/help - help menu"
    }
}
