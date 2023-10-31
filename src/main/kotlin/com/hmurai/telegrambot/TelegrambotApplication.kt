package com.hmurai.telegrambot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TelegrambotApplication

fun main(args: Array<String>) {
	runApplication<TelegrambotApplication>(*args)
}
