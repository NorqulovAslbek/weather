package com.example.simpletelegrambot.serviceImpl

import com.example.simpletelegrambot.entity.Users
import com.example.simpletelegrambot.enums.Step
import com.example.simpletelegrambot.repository.UserRepository
import com.example.simpletelegrambot.service.UserService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {
    override fun existUserByChatId(chatId: Long): Boolean {
        return userRepository.existsByChatId(chatId)
    }

    @Transactional
    override fun updateStep(chatId: Long, step: Step) {
        userRepository.updateStep(chatId, step)
    }

    override fun createUser(users: Users) {
        userRepository.save(users)
    }

    override fun getStepByUserChatId(chatId: Long): String? {
        return userRepository.getStepByUserChatId(chatId)
    }

    override fun updateUserName(chatId: Long, username: String) {
        userRepository.updateUserName(username,chatId)
    }

    override fun updateSurname(chatId: Long, surname: String) {
        userRepository.updateSurname(surname,chatId)
    }

    override fun updatePhone(chatId: Long, phone: String) {
        userRepository.updatePhone(phone,chatId)
    }
}