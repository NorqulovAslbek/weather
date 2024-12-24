package com.example.simpletelegrambot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class SimpleTelegramBotApplication

fun main(args: Array<String>) {
    runApplication<SimpleTelegramBotApplication>(*args)
}
