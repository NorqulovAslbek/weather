package com.example.simpletelegrambot.service

import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class WeatherService(
    private val restTemplate: RestTemplate
) {
    private val apiKeyWeather = "4c61eaff9349843b73c38cd341f52c81"
    fun getWeather(city: String): String {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKeyWeather&units=metric&lang=uz"
        val response = restTemplate.getForObject(url, Map::class.java)
        val main = response?.get("main") as Map<*, *>
        val weather = (response["weather"] as List<*>)[0] as Map<*, *>

        val temperature = main["temp"]
        val id: Int = (weather["id"] as Int?)!!
        val getEmoji = getWeatherEmoji(id)

        return "Shaharda: $getEmoji, harorat: $temperature°C"
    }

    fun getWeatherEmoji(weatherId: Int): String {
        return when (weatherId) {
            in 200..232 -> "⛈️" // Thunderstorm
            in 300..321 -> "🌦️" // Drizzle
            in 500..531 -> "🌧️" // Rain
            in 600..622 -> "🌨️" // Snow
            701 -> "🌫️" // Mist
            711 -> "💨" // Smoke
            721 -> "🌁" // Haze
            731, 761 -> "🌪️" // Dust
            741 -> "🌫️" // Fog
            751 -> "🏜️" // Sand
            762 -> "🌋" // Ash
            800 -> "☀️" // Clear
            in 801..804 -> "☁️" // Clouds
            else -> "❓" // Unknown
        }
    }

}