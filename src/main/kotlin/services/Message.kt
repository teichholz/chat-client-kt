package services

import kotlinx.datetime.LocalDateTime

sealed interface Sender {
    object Self : Sender
    data class Other(val user: OnlineUser) : Sender
}

data class Message(val content: String, val date: LocalDateTime, val sender: Sender)
