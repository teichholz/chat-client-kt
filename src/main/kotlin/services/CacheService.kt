package services

import model.Message
import model.OnlineUser

interface CacheService {
    fun initialize()

    fun cache(user: OnlineUser, messages: List<Message>)
    fun cache(entry: Pair<OnlineUser, List<Message>>) = cache(entry.first, entry.second)

    fun load(user: OnlineUser): List<Message>
}