package com.hmurai.telegrambot.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class NewsDTO(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticleDTO>
)
