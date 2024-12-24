package com.example.simpletelegrambot.repository

import com.example.simpletelegrambot.entity.Users
import com.example.simpletelegrambot.enums.Step
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<Users, Long> {
    fun existsByChatId(chatId: Long): Boolean

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.step = ?2 WHERE u.chatId = ?1")
    fun updateStep(chatId: Long, step: Step)

    @Query("select u.step from Users as u where u.chatId=?1")
    fun getStepByUserChatId(chatId: Long): String?

    @Modifying
    @Transactional
    @Query("update Users u set u.username=?1 where u.chatId=?2")
    fun updateUserName(userName: String, chatId: Long)

    @Modifying
    @Transactional
    @Query("update Users u set u.surname=?1 where u.chatId=?2")
    fun updateSurname(surname: String, chatId: Long)

    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.phone=?1 where u.chatId=?2")
    fun updatePhone(phone: String, chatId: Long)
}