package services

import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.CoroutineScope
import model.CurrentUser
import model.Message
import model.OnlineUser

interface MessageService {
    fun messagesFor(user: OnlineUser): List<Message>

    suspend fun sendMessage(to: OnlineUser, message: Message)

    context(DefaultClientWebSocketSession)
    suspend fun sendJob() {
    }

    context(DefaultClientWebSocketSession)
    suspend fun receiveJob() {
    }

    context(CoroutineScope)
    fun connectWebsockets(user: CurrentUser) {
    }
}