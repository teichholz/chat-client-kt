package screens

import Instances
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import logger.LoggerDelegate
import model.MainScreenModel
import model.Message
import model.OnlineUser
import model.Sender
import store
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainScreen : Screen {
    val logger by LoggerDelegate()
    override val key = uniqueScreenKey

    val model: MainScreenModel = MainScreenModel()

    @Composable
    override fun Content() {
        Column(Modifier.fillMaxHeight().fillMaxWidth().background(Color.Gray)) {
            Row(Modifier.background(Color.Gray)) {
                UserArea()
                ContentArea()
            }
        }

        LaunchedEffect(Unit) {
            Instances.messageService.connectWebsockets(store.currentUser)
        }
    }

    @Preview
    @Composable
    fun UserArea() {
        val userService = Instances.userService

        var users: List<OnlineUser> by remember { mutableStateOf(listOf()) }

        withVerticalScroll { scrollState ->
            Column(
                modifier = Modifier.fillMaxWidth(.2f)
                    .verticalScroll(scrollState)
            ) {
                UserSearch()
                users.forEach {
                    OnlineUserListItem(it)
                    Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
                }
            }
        }

        LaunchedEffect(Unit) {
            users = userService.getAllUsers()
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
    fun ContentArea() {
        if (model.selectedUser == null) {
            return
        }

        val messageService = Instances.messageService
        val coroutineScope = rememberCoroutineScope()

        val state by store.stateFlow.collectAsState()
        val selectedUser = model.selectedUser!!

        Column(Modifier.background(Color.Red).fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                state.messages[selectedUser]?.let {
                    Messages(it)
                }
            }
            SendMessage(modifier = Modifier.fillMaxHeight(.2f)) {
                val message = Message(
                    content = it,
                    date = LocalDateTime.now().toKotlinLocalDateTime(),
                    sender = Sender.Self
                )
                coroutineScope.launch {
                    messageService.sendMessage(selectedUser, message)
                }
            }
        }
    }

    @Composable
    fun Messages(messages: List<Message>) {
        val listState = rememberLazyListState()
        if (messages.isNotEmpty()) {
            LaunchedEffect(messages.last()) {
                listState.animateScrollToItem(messages.lastIndex, scrollOffset = 2)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(start = 4.dp, end = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = listState,
        ) {
            item { Spacer(Modifier.size(20.dp)) }
            items(messages) {
                ChatMessage(it)
            }
            item {
                Box(Modifier.height(70.dp))
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ChatMessage(message: Message) {
        val arrangement = if (message.sender == Sender.Self) Arrangement.End else Arrangement.Start
        Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = arrangement) {
            components.ChatMessage(message.sender) {
                ListItem(
                    modifier = Modifier.fillMaxWidth(.55f),
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