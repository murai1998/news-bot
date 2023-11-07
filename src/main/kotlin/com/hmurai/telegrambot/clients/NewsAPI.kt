package com.hmurai.telegrambot.clients
import com.hmurai.telegrambot.dto.NewsDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "news-api", url = "\${news-api.url}")
interface NewsAPI {

    @GetMapping(
        value = ["/top-headlines?country=us"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTopHeadlines(@RequestParam("apiKey") apiKey: String,
                        @RequestParam("apiKey") category: String? = null): NewsDTO
}
