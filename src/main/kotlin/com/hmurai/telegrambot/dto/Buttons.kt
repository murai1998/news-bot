package com.hmurai.telegrambot.dto

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

object Buttons {
    private val START_BUTTON = InlineKeyboardButton("Start")
    private val HELP_BUTTON = InlineKeyboardButton("Help")
   private val BREAKING_NEWS_BUTTON= InlineKeyboardButton("Breaking News")
    private val CUSTOMIZE_NEWS_BUTTON= InlineKeyboardButton("Customize news")
    fun inlineMarkup(): InlineKeyboardMarkup {
        START_BUTTON.callbackData = "/start"
        HELP_BUTTON.callbackData = "/help"
        BREAKING_NEWS_BUTTON.callbackData = "/breaking"
        CUSTOMIZE_NEWS_BUTTON.callbackData = "/customize"
        val rowInline = listOf(START_BUTTON, HELP_BUTTON, BREAKING_NEWS_BUTTON, CUSTOMIZE_NEWS_BUTTON)
        val rowsInLine = listOf(rowInline)
        val markupInline = InlineKeyboardMarkup()
        markupInline.keyboard = rowsInLine
        return markupInline
    }
}
