package services

import io.ktor.client.plugins.websocket.*
import model.Message
import model.OnlineUser

interface MessageService {
    fun messagesFor(user: OnlineUser): List<Message>

    suspend fun sendMessage(to: OnlineUser, message: Message)

    context(DefaultClientWebSocketSession)
    fun sendJob() {

    }

    context(DefaultClientWebSocketSession)
    fun receiveJob() {

    }
}