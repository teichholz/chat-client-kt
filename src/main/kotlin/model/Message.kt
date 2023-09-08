package model

import kotlinx.datetime.LocalDateTime

sealed interface Sender {
    data object Self : Sender
    data object Other : Sender
}

data class Message(val content: String, val date: LocalDateTime, val sender: Sender)
