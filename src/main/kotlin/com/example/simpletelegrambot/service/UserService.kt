package com.example.simpletelegrambot.service

import com.example.simpletelegrambot.entity.Users
import com.example.simpletelegrambot.enums.Step
import jakarta.transaction.Transactional

interface UserService {
    fun existUserByChatId(chatId: Long): Boolean
    @Transactional
    fun updateStep(chatId: Long, step: Step)
    fun createUser(users: Users)

    fun getStepByUserChatId(chatId: Long): String?

    fun updateUserName(chatId: Long, username: String)

    fun updateSurname(chatId: Long, surname: String)
    fun updatePhone(chatId: Long,phone:String)
}