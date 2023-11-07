package com.hmurai.telegrambot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients(basePackages = ["com.hmurai"])
@EnableCaching
@SpringBootApplication
class TelegrambotApplication

fun main(args: Array<String>) {
	runApplication<TelegrambotApplication>(*args)
}
