package com.hmurai.telegrambot

import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

@Component
class NewsBotController : TelegramLongPollingBot() {
    override fun getBotUsername(): String {
        return "breaking_news23_bot"
    }

    override fun getBotToken(): String {
        return ""
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            val msg = update.message
            val chatId = msg.chatId
            try {
                val reply = if (msg.text.contains("")) "" else ""
                sendNotification(chatId.toString(), reply)
            } catch (e: TelegramApiException) {
                throw RuntimeException(e)
            }
        }
    }

    @Throws(TelegramApiException::class)
    private fun sendNotification(chatId: String, msg: String) {
        val response = SendMessage(chatId, msg)
        execute(response)
    }
}
