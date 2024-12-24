package com.example.simpletelegrambot.controller

import com.example.simpletelegrambot.entity.Users
import com.example.simpletelegrambot.enums.Step
import com.example.simpletelegrambot.service.UserService
import com.example.simpletelegrambot.service.WeatherService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.LinkedList

@Component
class BotHandler(
    private val userService: UserService,
    private val weatherService: WeatherService
) : TelegramLongPollingBot() {

    private val regions = listOf(
        "Tashkent", "Samarkand", "Bukhara", "Andijan", "Fergana",
        "Namangan", "Khiva", "Karshi", "Termez", "Nukus", "Jizzakh"
    )


    private val removeMap = mutableMapOf<Long, Int>()


    override fun getBotUsername(): String {
        return "telegram bot username"
    }

    override fun getBotToken(): String {
        return "telegram api key"
    }

    override fun onUpdateReceived(update: Update?) {
        if (update != null && update.hasMessage()) {
            val message = update.message
            when {
                message.hasText() -> handleTextMessage(message)
                message.hasContact() -> handleContactMessage(message)
            }
        } else if (update != null && update.hasCallbackQuery()) {
            handleCallbackQuery(update.callbackQuery)
        }
    }

    private fun handleCallbackQuery(callbackQuery: CallbackQuery) {
        // Tugma bosilgandan keyingi holatni o'chirish
        execute(AnswerCallbackQuery().apply {
            callbackQueryId = callbackQuery.id
            text = "Siz ${callbackQuery.data}ni tanladingiz!"
            showAlert = false // Alert chiqarishni xohlamasangiz, false qiling
        })
        val callbackData = callbackQuery.data
        val chatId = callbackQuery.message.chatId
        val weather = weatherService.getWeather(callbackData)
        executeText(chatId, weather)
    }

    private fun handleContactMessage(message: Message) {
        val chatId = message.chatId
        val contact = message.contact
        userService.updatePhone(chatId, contact.phoneNumber)
        val messageId = removeMap[chatId]
        if (messageId != null) {
            deleteMessage(chatId, messageId)
            removeMap.remove(chatId)
        }
        deleteMessage(chatId, message.messageId)
        userService.updateStep(chatId, Step.WORK)
        sendRegionInlineKeyboardButton(chatId)
    }


    private fun handleTextMessage(message: Message) {
        val chatId = message.chatId
        val exists = userService.existUserByChatId(chatId)  //bu yoda null ham kelishi mumkun
        val userStep = userService.getStepByUserChatId(chatId)
        if (message.text.equals("/start") && !exists) {
            userService.createUser(Users(chatId))
            userService.updateStep(chatId, Step.NAME)
            val messageId = executeText(
                chatId,
                "\uD83D\uDC4B Assalom alaykum botimizga hush kelibsiz.\nIsmingizni kiriting ✏\uFE0F"
            )
            removeMap[chatId] = messageId  // bu yani chatId ga messageId ni ornatib qoydi
        } else if (userStep == Step.NAME.toString() && message.text.toCharArray()[0] != '/') {
            userService.updateUserName(chatId, message.text)
            userService.updateStep(chatId, Step.SURNAME)
            val getMessageId = removeMap[chatId]
            if (getMessageId != null) {
                deleteMessage(chatId, getMessageId)
                removeMap.remove(chatId)
            }
            deleteMessage(chatId, message.messageId) /// bu ismini kritgandan keyin ismni ochirish uchun
            val messageId = executeText(chatId, "Familiyangizni kiriting \uD83D\uDD8C")
            removeMap[chatId] = messageId
        } else if (userStep == Step.SURNAME.toString() && message.text.toCharArray()[0] != '/') {
            userService.updateSurname(chatId, message.text)
            userService.updateStep(chatId, Step.PHONE)
            val messageId = removeMap[chatId]
            if (messageId != null) {
                deleteMessage(chatId, messageId)
                removeMap.remove(chatId)
            }
            deleteMessage(chatId, message.messageId)
            sendPhoneNumberRequest(chatId)
        }else if(userStep==Step.WORK.toString()){
            sendRegionInlineKeyboardButton(chatId)
        }else {

            /*   when (userStep) {
                   "NAME" -> executeText(chatId, "hato buyuruq kiritingiz! Ismingizni kiriting  ✏\uFE0F")
                   "SURNAME" -> executeText(
                       chatId,
                       "hato buyuruq kiritingiz! Familiyangizni kiriting  \uD83D\\uDD8"
                   )
               }
             */
        }
    }

    fun executeText(chatId: Long, text: String): Int {
        val execute = execute(SendMessage(chatId.toString(), text))
        return execute.messageId
    }

    private fun sendPhoneNumberRequest(chatId: Long) {

        val contactButton = KeyboardButton("Telefon raqamimni yuborish \uD83D\uDCF1").apply {
            requestContact = true
        }
        val keyboardRow = KeyboardRow().apply {
            add(contactButton)
        }

        val replyKeyboardMarkup = ReplyKeyboardMarkup().apply {
            keyboard = listOf(keyboardRow)
            oneTimeKeyboard = true
            resizeKeyboard = true
        }

        val sendMarkup = SendMessage().apply {
            this.chatId = chatId.toString()
            replyMarkup = replyKeyboardMarkup
            text = "Telefon raqamingizni yuboring \uD83D\uDCF1"
        }

        val sendMessage = execute(sendMarkup)
        removeMap[chatId] = sendMessage.messageId
    }

    private fun sendRegionInlineKeyboardButton(chatId: Long) {
        val regionCount: Int = regions.size
        val listColumn = LinkedList<List<InlineKeyboardButton>>()
        var listRow = LinkedList<InlineKeyboardButton>()

        for (i in 0 until regionCount) {
            listRow.add(
                InlineKeyboardButton().apply {
                    text = regions[i]
                    callbackData = regions[i]
                }
            )
            if (i % 2 == 1 || i == regionCount - 1) {
                listColumn.add(listRow)
                listRow = LinkedList()
            }
        }

        val inlineKeyboardMarkup = InlineKeyboardMarkup().apply {
            keyboard = listColumn
        }

        val sendMarkup = SendMessage().apply {
            this.chatId = chatId.toString()
            text = "Viloyatingizni tanlang \uD83C\uDF0D"
            replyMarkup = inlineKeyboardMarkup
        }

        execute(sendMarkup)
    }


    private fun deleteMessage(chatId: Long, messageId: Int) {
        try {
            execute(
                DeleteMessage(
                    chatId.toString(),
                    messageId
                )
            )
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

}
