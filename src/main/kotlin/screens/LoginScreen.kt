package screens

import Instances
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import logger.LoggerDelegate
import okio.Path.Companion.toPath
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class LoginScreen : Screen {
    val logger by LoggerDelegate()

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val userService = Instances.userService

        var name: String by remember { mutableStateOf("") }

        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.width(IntrinsicSize.Max),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    name,
                    singleLine = true,
                    modifier = Modifier.onKeyEvent {
                        if (it.key == Key.Enter) {
                            scope.launch {
                                userService.login(name)
                                navigator.replace(MainScreen())
                            }
                        }
                        true
                    },
                    onValueChange = { name = it },
                    label = { Text("Enter name") })

                Row(modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.Main) {
                                userService.register(name)
                                navigator.replace(MainScreen())
                            }
                        },
                    ) {
                        Text("Register")
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                userService.login(name)
                                navigator.replace(MainScreen())
                            }
                        },
                    ) {
                        Text("Login")
                    }
                }
            }
        }
    }

    fun getAppDirectory(email: String): Path {
        val appDataDirectory = getAppDataDirectory()
        val path = "$appDataDirectory/ChatApp-$email".toPath()


        return Files.createDirectory(path.toNioPath())

        // FileSystem.SYSTEM
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