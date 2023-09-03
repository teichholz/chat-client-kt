import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import services.CurrentUser
import services.OnlineUser

object State {
    val currentUser: MutableState<CurrentUser?> = mutableStateOf(null)

    val userSearch: MutableState<String> = mutableStateOf("")
    val selectedUser: MutableState<OnlineUser?> = mutableStateOf(null)
}
