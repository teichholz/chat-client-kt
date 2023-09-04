package screens

import Action
import Instances
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import components.SendMessage
import components.withVerticalScroll
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import logger.LoggerDelegate
import model.MainScreenModel
import services.Message
import services.OnlineUser
import services.Sender
import store
import java.time.format.DateTimeFormatter

class MainScreen : Screen {
    val logger by LoggerDelegate()

    override val key = uniqueScreenKey

    val model: MainScreenModel = MainScreenModel()

    @Composable
    override fun Content() {
        //val navigator = LocalNavigator.currentOrThrow

        Column(Modifier.fillMaxHeight().fillMaxWidth().background(Color.Gray)) {
            Row(Modifier.background(Color.Gray)) {
                UserArea()
                ContentArea()
            }
        }
    }

    @Preview
    @Composable
    fun UserArea() {
        val userService = Instances.userService

        withVerticalScroll { scrollState ->
            Column(
                modifier = Modifier.fillMaxWidth(.2f)
                    .verticalScroll(scrollState)
            ) {
                UserSearch()
                userService.getAllUsers().forEach {
                    OnlineUserListItem(it)
                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
                }
            }
        }
    }

    @Composable
    @Preview
    fun UserSearch() {
        TextField(
            model.userSearch,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = { model.setUserSearch(it) },
            label = { Text("Search User") })
    }

    @OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    fun OnlineUserListItem(user: OnlineUser) {
        var inside by remember { mutableStateOf(false) }
        ListItem(icon = {
            Icon(
                painter = painterResource("user-128.png"),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(.2f)
            )
        }, modifier = Modifier.onPointerEvent(PointerEventType.Enter) {
            inside = true
        }.onPointerEvent(PointerEventType.Exit) {
            inside = false
        }.onClick {
            model.setSelectedUser(user)
        }) {
            Text(user.name, fontSize = if (inside) 25.sp else 20.sp)
        }
    }

    @Composable
    fun RowScope.ContentArea() {
        val messageService = Instances.messageService

        if (model.selectedUser == null) {
            return
        }

        logger.info("Contentent Area compose")

        Column(Modifier.background(Color.Red).weight(1f).fillMaxHeight()) {
            withVerticalScroll { scrollState ->
                Column(
                    modifier = Modifier.verticalScroll(scrollState)
                ) {
                    messageService.messagesFor(OnlineUser("online user", Any())).forEach { message ->
                        ChatMessage(message)
                    }
                }
            }
            val selectedUser = model.selectedUser!!
            SendMessage(modifier = Modifier.weight(.2f)) {
                store.send(
                    Action.SendMessage(
                        selectedUser,
                        Message(
                            content = it,
                            date = java.time.LocalDateTime.now().toKotlinLocalDateTime(),
                            sender = Sender.Self
                        )
                    )
                )
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ChatMessage(message: Message) {
        val arrangement = if (message.sender == Sender.Self) Arrangement.End else Arrangement.Start
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = arrangement) {
            Card(shape = RoundedCornerShape(10.dp), elevation = 10.dp) {
                ListItem(
                    modifier = Modifier.fillMaxWidth(.55f)
                        .background(Color.Cyan),
                    trailing = {
                        Text(
                            message.date.toJavaLocalDateTime().toLocalTime()
                                .format(DateTimeFormatter.ofPattern("HH:mm"))
                        )
                    }
                ) {
                    Text(message.content)
                }
            }
        }
    }
}