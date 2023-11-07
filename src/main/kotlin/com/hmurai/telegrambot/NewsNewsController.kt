package com.hmurai.telegrambot

import com.hmurai.telegrambot.configuration.BotConfig
import com.hmurai.telegrambot.dto.NewsCommands
import com.hmurai.telegrambot.dto.NewsCommands.Companion.LIST_OF_COMMANDS
import com.hmurai.telegrambot.dto.UserDTO
import com.hmurai.telegrambot.repository.UserRepository
import com.hmurai.telegrambot.service.NewsBotService
import com.hmurai.telegrambot.service.NewsBotService.Companion.newsCategories
import com.hmurai.telegrambot.service.NewsBotService.Companion.newsCategoryId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.*

@Service
class NewsNewsController @Autowired constructor(
    private val botConfig: BotConfig, private val newsBotService: NewsBotService,
    private val userRepository: UserRepository
) : TelegramLongPollingBot(), NewsCommands {
    final val logger: Logger = LoggerFactory.getLogger(this.javaClass)
    var isConfirmationRequestedOnNewsPreferences = false

    init {
        try {
            this.execute(SetMyCommands(LIST_OF_COMMANDS, BotCommandScopeDefault(), null))
        } catch (ex: TelegramApiException) {
            logger.error("Failed to initiate NewsBot", ex)
        }
    }

    override fun getBotUsername(): String {
        return botConfig.botName!!
    }

    override fun getBotToken(): String {
        return botConfig.token!!
    }

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            sendNotification(update.message)
        } else if (update.hasCallbackQuery()) {
            onNewsPreferencesUpdate(update.callbackQuery)
        }
    }

    @Throws(TelegramApiException::class)
    private fun sendNotification(receivedMessage: Message) {
        val userDto = userRepository.getUserDTO(receivedMessage.from.id.toString())
        when (receivedMessage.text) {
            "/breaking" -> sendBreakingNews(receivedMessage.chatId.toString())
            "/customize" -> setUpCustomPreferences(chatId = receivedMessage.chatId.toString(), userDto = userDto)
            else -> {}
        }
    }

    private fun sendBreakingNews(chatId: String) {
        val breakingNews = newsBotService.getTopHeadlines()
        if (breakingNews.isNotEmpty()) {
            breakingNews.filter { !it.description.isNullOrBlank() || !it.title.isNullOrBlank() }.subList(0, 5)
                .forEach { article ->
                    val caption = """
                    ${article.description ?: article.title}
                    [Read article >>>](${article.url})
                """.trimIndent()
                    if (!article.urlToImage.isNullOrBlank()) {
                        val sendPhoto = SendPhoto()
                        val imageFile = InputFile(article.urlToImage)
                        sendPhoto.chatId = chatId
                        sendPhoto.photo = imageFile
                        sendPhoto.caption = caption
                        sendPhoto.parseMode = "Markdown"
                        execute(sendPhoto)
                    } else {
                        sendTextMessage(chatId = chatId, textMsg = caption, parseMode = "Markdown")
                    }
                }
        }
    }

    private fun setUpCustomPreferences(chatId: String, userDto: UserDTO? = null, messageId: Int? = null) {
        try {
            val keyboard = InlineKeyboardMarkup()
            val selectedCategories = userDto?.selectedNewsCategories ?: listOf()
            val rows = newsCategories.map { option ->
                val isSelected = selectedCategories.contains(option.key)
                val buttonText = if (isSelected) "${option.value}: ✅" else "${option.value}: ❌"
                listOf(
                    InlineKeyboardButton.builder()
                        .text(buttonText)
                        .callbackData("$newsCategoryId~${option.key}")
                        .build()
                )

            }
            val confirmButton = InlineKeyboardButton.builder()
                .text("---Confirm your Selection---")
                .callbackData("$newsCategoryId:confirm")
                .build()
            keyboard.keyboard = rows + listOf(listOf(confirmButton))
            val description = "Select one or more categories you want to see news for:"
            if (messageId != null) {
                val updatedMessage = EditMessageText()
                updatedMessage.chatId = chatId
                updatedMessage.messageId = messageId
                updatedMessage.text = description
                updatedMessage.replyMarkup = keyboard
                execute(updatedMessage)
            } else {
                sendTextMessage(chatId = chatId, textMsg = description, replyMarkup = keyboard)
            }
        } catch (e: TelegramApiException) {
            logger.error("Failed to set up custom preferences", e)
        }
    }

    private fun onNewsPreferencesUpdate(callbackQuery: CallbackQuery) {
        val callbackQueryData = callbackQuery.data
        val userFrom = callbackQuery.from
        val userDto = userRepository.getUserDTO(userFrom.id.toString()) ?: UserDTO(
            userId = userFrom.id.toString(),
            firstName = userFrom.firstName,
            lastName = userFrom.lastName,
            createdAt = Date(),
            updatedAt = Date()
        )
        if (callbackQueryData == "$newsCategoryId:confirm") {
            if (isConfirmationRequestedOnNewsPreferences) {
                userDto.lockedSelectedNewsCategories = userDto.selectedNewsCategories
                val response =
                    "Great news!\nYour updated list of news categories now includes: <b>${
                        userDto.selectedNewsCategories.joinToString(
                            ", "
                        )
                    }</b>"
                sendTextMessage(
                    chatId = callbackQuery.message.chatId.toString(),
                    textMsg = response,
                    parseMode = "HTML"
                )
                isConfirmationRequestedOnNewsPreferences = false
            }
        } else if (callbackQueryData.startsWith(newsCategoryId)) {
            val selectedOption = callbackQueryData.replace("$newsCategoryId~", "")
            // Toggle the selection of an option
            if (userDto.selectedNewsCategories.contains(selectedOption)) {
                userDto.selectedNewsCategories.remove(selectedOption)
            } else {
                userDto.selectedNewsCategories.add(selectedOption)
            }
            isConfirmationRequestedOnNewsPreferences = true
            // Update the message to reflect the selection
            setUpCustomPreferences(
                chatId = callbackQuery.message.chatId.toString(),
                userDto = userDto,
                messageId = callbackQuery.message.messageId
            )
        }
        userDto.updatedAt = Date()
        userRepository.updateUserDTO(userDto)
    }

    fun sendTextMessage(
        chatId: String,
        textMsg: String,
        replyMarkup: InlineKeyboardMarkup? = null,
        parseMode: String? = null
    ) {
        val message = SendMessage()
        message.chatId = chatId
        message.text = textMsg
        if (replyMarkup != null) {
            message.replyMarkup = replyMarkup
        }
        if (!parseMode.isNullOrBlank()) {
            message.parseMode = parseMode
        }
        try {
            execute(message)
        } catch (e: TelegramApiException) {
            logger.error("Failed to send text message with chatId: $chatId", e)
        }
    }
}
