import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import logger.getLogger
import model.CurrentUser
import model.Message
import model.OnlineUser
import model.Sender
import model.confirm

val logger = getLogger("Store")

object EmptyStore : Store {
    override fun send(action: Action) {
        throw NotImplementedError()
    }

    override val stateFlow: StateFlow<State>
        get() = throw NotImplementedError()
}

fun CoroutineScope.createStore(user: CurrentUser): Store {
    val mutableStateFlow = MutableStateFlow(State(user))
    val channel: Channel<Action> = Channel(Channel.UNLIMITED)

    return object : Store {
        init {
            launch {
                channel.consumeAsFlow().collect { action ->
                    mutableStateFlow.value = reducer(mutableStateFlow.value, action)
                }
            }
        }

        override fun send(action: Action) {
            launch {
                channel.send(action)
            }
        }

        override val stateFlow: StateFlow<State> = mutableStateFlow
    }
}

fun reducer(state: State, action: Action): State =
    when (action) {
        is Action.SendMessage -> {
            if (action.message.sender != Sender.Self) {
                throw IllegalArgumentException("Cannot send message from other user")
            }

            val newMessages = (state.messages[action.to] ?: listOf()) + action.message
            state.copy(
                sent = state.sent + 1,
                messages = state.messages + (action.to to newMessages)
            )
        }

        is Action.ConfirmSentMessage -> {
            val messages = state.messages[action.to]?.toMutableList() ?: throw IllegalArgumentException("Cannot confirm message for unknown user")

            val index = messages.indexOfFirst { it.content == action.message.content }
            if (messages[index].sender == Sender.Other) {
                throw IllegalArgumentException("Cannot confirm message from other user")
            }
            messages[index] = messages[index].confirm()

            state.copy(
                messages = state.messages + (action.to to messages)
            )
        }

        is Action.ReceiveMessage -> {
            if (action.message.sender == Sender.Self) {
                throw IllegalArgumentException("Cannot receive message from self")
            }

            val newMessages = (state.messages[action.from] ?: listOf()) + action.message
            state.copy(
                received = state.received + 1,
                messages = state.messages + (action.from to newMessages)
            )
        }
    }

interface Store {
    fun send(action: Action)
    val stateFlow: StateFlow<State>
    val state get() = stateFlow.value

    val currentUser get() = state.currentUser
    val messages get() = state.messages
    val received get() = state.received
    val sent get() = state.sent
}

data class State(
    var currentUser: CurrentUser,

    val received: Long = 0,
    val sent: Long = 0,
    val messages: Map<OnlineUser, List<Message>> = mapOf()
)

sealed interface Action {
    data class SendMessage(val to: OnlineUser, val message: Message) : Action
    data class ConfirmSentMessage(val to: OnlineUser, val message: Message) : Action
    data class ReceiveMessage(val from: OnlineUser, val message: Message) : Action
}
