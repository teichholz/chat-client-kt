package services

import EmptyStore
import Store
import model.Message
import model.OnlineUser

interface CacheService {
    val store: Store

    fun cache() {
        require(store !is EmptyStore) { "Store must be initialized" }

        store.state.messages.forEach { (user, messages) ->
            cache(user, messages)
        }
    }
    fun cache(entry: Pair<OnlineUser, List<Message>>) = cache(entry.first, entry.second)
    fun cache(user: OnlineUser, messages: List<Message>)

    fun load(user: OnlineUser)
}