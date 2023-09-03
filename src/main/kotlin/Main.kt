import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import screens.LoginScreen

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            Navigator(LoginScreen()) {
                MaterialTheme {
                    Scaffold(
                        topBar = { TopAppBar(Modifier.fillMaxHeight(.1f).fillMaxWidth()) { Text("Top App Bar") } }
                    ) {
                        CurrentScreen()
                    }
                }
            }
        }
    }
}
