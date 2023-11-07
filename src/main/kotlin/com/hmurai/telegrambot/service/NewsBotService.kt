package com.hmurai.telegrambot.service

import com.hmurai.telegrambot.clients.NewsAPI
import com.hmurai.telegrambot.dto.NewsArticleDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.util.*

@Service
class NewsBotService(
    private val newsAPI: NewsAPI,
    private val cacheManager: CacheManager
) {
    @Value("\${news-api.token}")
    lateinit var newsApiToken: String
    final val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @Cacheable("top-headlines")
    fun getTopHeadlines(category: String? = null): List<NewsArticleDTO> {
        try {
            logger.info("Searching for $category")
            return newsAPI.getTopHeadlines(newsApiToken).articles.filter {
                !it.url.isNullOrBlank()
            }
        } catch (ex: Exception) {
            logger.error("Failed to fetch top headlines", ex)
        }
        return listOf()
    }

    @Scheduled(initialDelay = 0, fixedDelay = (60 * 60 * 1000).toLong())
    fun expireCacheEveryHour() {
        val cacheNames = listOf("top-headlines")
        cacheManager.cacheNames
            .forEach { cacheName: String ->
                if (cacheNames.any { it.equals(cacheName, ignoreCase = true) }) {
                    cacheManager.getCache(cacheName)?.clear()
                }
            }
    }

    companion object {
        val newsCategories = mapOf(
            "business" to "\uD83D\uDCBC Business",
            "entertainment" to "\uD83C\uDF7F Entertainment",
            "health" to "\uD83C\uDFCB\uD83C\uDFFB Health",
            "science" to "\uD83E\uDDEC Science",
            "sports" to "\uD83C\uDFC8 Sports",
            "technology" to "\uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBB Technology"
        )

        val newsCategoryId = UUID.randomUUID().toString()
    }
}
