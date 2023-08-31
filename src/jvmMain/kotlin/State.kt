import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import services.User

object State {
    val currentUser: MutableState<User?> = mutableStateOf(null)

    val userSearch: MutableState<String> = mutableStateOf("")
    val selectedUser: MutableState<User?> = mutableStateOf(null)
}
