package com.example.simpletelegrambot.config

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class ApiConfig {
    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}