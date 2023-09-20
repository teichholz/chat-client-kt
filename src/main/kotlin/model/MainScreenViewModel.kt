package model

import Instances
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.seconds

data class State(
    val userSearch: String = "",
    val selectedUser: OnlineUser? = null
)

class MainScreenViewModel : StateScreenModel<State>(State()) {
    fun setUserSearch(userSearch: String) {
        mutableState.update {
            it.copy(userSearch = userSearch)
        }
    }

    val userSearch @Composable get() = mutableState.collectAsState().value.userSearch

    fun setSelectedUser(selectedUser: OnlineUser?) {
        mutableState.update {
            it.copy(selectedUser = selectedUser)
        }
    }

    val selectedUser @Composable get() = mutableState.collectAsState().value.selectedUser

    val onlineUsers = flow {
        while (true) {
            emit(Instances.userService.getAllUsers())
            delay(5.seconds)
        }
    }

    val latestOnlineUsers @Composable get() = onlineUsers.collectAsState(emptyList()).value

    val filteredLatestOnlineUsers: List<OnlineUser>
        @Composable get() {
            val userSearch = userSearch
            return onlineUsers.map {
                if (userSearch.isBlank())
                    it
                else
                    it.filter {
                        it.name.contains(userSearch, ignoreCase = true)
                    }
            }.collectAsState(emptyList()).value
        }
}