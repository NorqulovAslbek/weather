package com.example.simpletelegrambot.config

import com.example.simpletelegrambot.controller.BotHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class BotConfig(
    private val botHandler: BotHandler
) {
    @Bean
    fun botSession(): DefaultBotSession {
        TelegramBotsApi(DefaultBotSession::class.java).registerBot(botHandler)
        return DefaultBotSession()
    }

}