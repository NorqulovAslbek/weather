package com.example.simpletelegrambot.entity

import com.example.simpletelegrambot.enums.Step
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class Users(
    @Column(nullable = false)
    var chatId: Long,
    var username: String? = null,
    var surname: String? = null,
    var phone: String? = null,
    @Enumerated(EnumType.STRING)
    var step: Step? = null
) : BaseEntity()