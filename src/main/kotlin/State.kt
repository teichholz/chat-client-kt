import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import logger.getLogger
import services.CurrentUser
import services.Message
import services.OnlineUser

val logger = getLogger("Store")

fun CoroutineScope.createStore(): Store {
    val mutableStateFlow = MutableStateFlow(State())
    val channel: Channel<Action> = Channel(Channel.UNLIMITED)

    return object : Store {
        init {
            launch {
                channel.consumeAsFlow().collect { action ->
                    mutableStateFlow.value = reducer(mutableStateFlow.value, action)
                    logger.info("Performed State Action: $action")
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
            val newMessages = (state.messages[action.to] ?: listOf()) + action.message
            state.copy(
                messages = state.messages + (action.to to newMessages)
            )
        }

        is Action.Login -> {
            state.copy(
                currentUser = action.user
            )
        }
    }

interface Store {
    fun send(action: Action)
    val stateFlow: StateFlow<State>
    val state get() = stateFlow.value

    val currentUser get() = state.currentUser
    val messages get() = state.messages
}

data class State(
    val currentUser: CurrentUser? = null,
    val messages: Map<OnlineUser, List<Message>> = mapOf()
)

sealed interface Action {
    data class Login(val user: CurrentUser) : Action
    data class SendMessage(val to: OnlineUser, val message: Message): Action
}
