package model

import kotlinx.datetime.LocalDateTime

sealed interface Sender {
    object Self : Sender {
        override fun toString(): String = "Self"
    }
    object Other : Sender {
        override fun toString(): String = "Other"
    }
}

data class Message(val content: String, val date: LocalDateTime, val sender: Sender)
