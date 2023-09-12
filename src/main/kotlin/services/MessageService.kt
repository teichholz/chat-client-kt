package services

import Action
import Instances
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.datetime.toKotlinLocalDateTime
import model.CurrentUser
import model.Message
import model.OnlineUser
import model.Sender
import store
import java.time.LocalDateTime

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
    suspend fun connectWebsockets(user: CurrentUser) {
        var counter = 0
        while (true) {
            Instances.userService.getAllUsers().forEach {
                store.send(
                    Action.ReceiveMessage(
                        it,
                        Message("Test message ${counter} from ${it.name}", LocalDateTime.now().toKotlinLocalDateTime(), Sender.Other)
                    )
                )
            }
            counter++
            delay(1000)
        }
    }
}