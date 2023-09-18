package screens

import Instances
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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

class LoginScreen : Screen {
    val logger by LoggerDelegate()

    override val key = uniqueScreenKey

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val userService = Instances.userService

        val scope = rememberCoroutineScope()
        var name: String by remember { mutableStateOf("") }
        val loginFocus by remember { mutableStateOf(FocusRequester()) }

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
                    modifier = Modifier.focusRequester(loginFocus).onKeyEvent {
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

        LaunchedEffect(Unit) {
            loginFocus.requestFocus()
        }
    }
}