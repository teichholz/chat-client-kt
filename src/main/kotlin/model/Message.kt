package model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
sealed interface Sender {
    companion object {
        fun confirmedSelf() = Self.apply { confirmed = true }
    }

    @Serializable
    data object Self : Sender {
        var confirmed = false

    }
    @Serializable
    data object Other : Sender
}


@Serializable
data class Message(val content: String, val date: LocalDateTime, val sender: Sender)

fun Message.confirm() = copy(sender = Sender.confirmedSelf())
