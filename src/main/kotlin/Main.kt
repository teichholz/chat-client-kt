import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import screens.LoginScreen
import kotlin.properties.Delegates

var store: Store by Delegates.observable(EmptyStore) { _, _, new ->
    canLogout = new !is EmptyStore
}

var canLogout by mutableStateOf(false)

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            Navigator(LoginScreen()) {
                MaterialTheme {
                    Scaffold(
                        topBar = {
                            TopAppBar(Modifier.fillMaxHeight(.1f).fillMaxWidth()) {
                                Row(modifier = Modifier.fillMaxWidth(1f),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Top App Bar")
                                    if (canLogout) {
                                        val navigator = LocalNavigator.currentOrThrow
                                        IconButton({
                                            resetApplication()
                                            navigator.replace(LoginScreen())
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.ExitToApp,
                                                contentDescription = "Logout",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        CurrentScreen()
                    }
                }
            }
        }
    }
}
