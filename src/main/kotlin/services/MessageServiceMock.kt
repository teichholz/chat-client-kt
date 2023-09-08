package services

import Action
import kotlinx.datetime.toKotlinLocalDateTime
import model.Message
import model.OnlineUser
import model.Sender
import store
import java.time.LocalDateTime

class MessageServiceMock : MessageService {
    override fun messagesFor(user: OnlineUser): List<Message> {
        return (0..15).map {
            Message(
                "Message $it: ${user.name}",
                LocalDateTime.now().plusHours(it.toLong()).toKotlinLocalDateTime(),
                if (it % 2 == 0) Sender.Self else Sender.Other
            )
        }
    }

    override suspend fun sendMessage(to: OnlineUser, message: Message) {
        store.send(Action.SendMessage(to, message))
    }
}