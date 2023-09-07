package model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.core.model.StateScreenModel
import kotlinx.coroutines.flow.update

data class State(
    val userSearch: String = "",
    val selectedUser: OnlineUser? = null
)

sealed class MainScreenModel : StateScreenModel<State>(State()) {
    fun setUserSearch(userSearch: String) {
        mutableState.update {
            it.copy(userSearch = userSearch)
        }
    }

    val userSearch @Composable get () = mutableState.collectAsState().value.userSearch

    fun setSelectedUser(selectedUser: OnlineUser?) {
        mutableState.update {
            it.copy(selectedUser = selectedUser)
        }
    }

    val selectedUser @Composable get () = mutableState.collectAsState().value.selectedUser
}