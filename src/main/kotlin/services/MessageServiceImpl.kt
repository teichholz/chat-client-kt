package services

import Action
import arrow.atomic.AtomicInt
import chat.commons.protocol.MessagePayloadSocket
import chat.commons.protocol.Protocol
import chat.commons.protocol.ack
import chat.commons.protocol.isMessage
import chat.commons.protocol.message
import chat.commons.routing.ReceiverPayload
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalDateTime
import model.Message
import model.OnlineUser
import model.Sender
import store
import java.time.LocalDateTime

class MessageServiceImpl : MessageService {
    val messageQueue: Channel<Protocol.MESSAGE> = Channel(Channel.UNLIMITED)

    override fun messagesFor(user: OnlineUser): List<Message> {
        return (0..15).map {
            Message("Message $it: ${user.name}",
                LocalDateTime.now().plusHours(it.toLong()).toKotlinLocalDateTime(),
                if (it % 2 == 0) Sender.Self else Sender.Other)
        }
    }

    override suspend fun sendMessage(to: OnlineUser, message: Message) {
        val protocolMessage = message {
            payload = MessagePayloadSocket(
                from = ReceiverPayload(store.currentUser.name),
                to = ReceiverPayload(to.name),
                message = message.content,
                sent = message.date
            )
        } as Protocol.MESSAGE
        messageQueue.send(protocolMessage)
        store.send(Action.SendMessage(to, message))
    }

    context(DefaultClientWebSocketSession)
    override fun sendJob() {
        val sent = AtomicInt(0)
        launch(Dispatchers.IO) {
            while (isActive) {
                val message = messageQueue.receive()
                sendSerialized(message)
                sent.incrementAndGet()
            }
        }
    }

    context(DefaultClientWebSocketSession)
    override fun receiveJob() {
        launch {
            while (isActive) {
                val protocol = receiveDeserialized<Protocol>()

                if (!protocol.isMessage()) {
                    throw IllegalStateException("Received non-message protocol")
                }

                protocol.message {
                    val from = it.payload.from.name
                    val to = it.payload.to.name
                    val content = it.payload.message
                    val sent = it.payload.sent

                    if (from == store.currentUser.name) {
                        throw IllegalStateException("Received message from self")
                        //store.send(Action.SendMessage(OnlineUser(to, Any()), Message(content = content, date = sent, sender = Sender.Self)))
                    } else {
                        store.send(
                            Action.ReceiveMessage(
                                OnlineUser(from, Any()),
                                Message(content = content, date = sent, sender = Sender.Other)
                            )
                        )
                    }
                }

                sendSerialized(ack {
                    payload = store.received
                })
            }
        }
    }
}