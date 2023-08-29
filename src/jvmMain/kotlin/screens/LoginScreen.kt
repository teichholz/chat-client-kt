package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import services.UserService
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class LoginScreen : Screen {
    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val userService = UserService()

        var email: String by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            TextField(
                email,
                modifier = Modifier,
                onValueChange = { email = it },
                label = { Text("E-Mail Address") })


            Button(onClick = { navigator.replace(MainScreen()) }, enabled = !userService.isEmailTaken(email)) {
                Text("Login")
            }
        }
    }

    fun getAppDirectory(email: String): Path {
        val appDataDirectory = getAppDataDirectory()
        val file = File("$appDataDirectory/ChatApp-$email")

        return Files.createDirectory(file.toPath())

        /*file.sink().buffer().use { sink ->
            for ((key, value) in System.getenv()) {
                sink.writeUtf8(key)
                sink.writeUtf8("=")
                sink.writeUtf8(value)
                sink.writeUtf8("\n")
            }
        }*/
    }

    fun getAppDataDirectory(): String {
        val os = System.getProperty("os.name").lowercase(Locale.getDefault())

        val appDataDir: String = when {
            os.contains("win") -> System.getenv("APPDATA")
            os.contains("mac") -> "${System.getProperty("user.home")}/Library/Application Support"
            os.contains("nix") || os.contains("nux") || os.contains("sunos") ->
                "${System.getProperty("user.home")}/.appdata"
            else -> throw IllegalStateException("Unsupported OS: $os")
        }

        return appDataDir
    }
}