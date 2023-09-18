package services

import Action
import model.Message
import model.OnlineUser
import store

class MessageServiceMock : MessageService {
    override suspend fun sendMessage(to: OnlineUser, message: Message) {
        store.send(Action.SendMessage(to, message))
    }
}