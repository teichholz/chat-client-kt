package services

import kotlinx.datetime.toKotlinLocalDateTime

class MessageService {
    fun messagesFor(user: OnlineUser): List<Message> {
        return (0..15).map {
            Message("Message $it: ${user.name}",
                java.time.LocalDateTime.now().plusHours(it.toLong()).toKotlinLocalDateTime(),
                if (it % 2 == 0) Sender.Self else Sender.Other(OnlineUser("Other User", Any())))
        }
    }
}