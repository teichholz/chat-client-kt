import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.datetime.toJavaLocalDateTime
import services.MessageService
import services.Sender
import services.User
import services.UserService
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
@Preview
fun App() {
    var userSearch by remember { mutableStateOf("") }
    var selectedUser: User? by remember { mutableStateOf(null) }

    val userService = UserService()
    val messageService = MessageService()

    MaterialTheme {
        Scaffold(
            topBar = { TopAppBar(Modifier.fillMaxHeight(.1f).fillMaxWidth()) { Text("Top App Bar") } }
        ) { padding ->
            Column(Modifier.fillMaxHeight().fillMaxWidth().background(Color.Gray)) {
                Row(Modifier.background(Color.Gray)) {
                    val stateVertical = rememberScrollState(0)
                    Column(
                        modifier = Modifier.fillMaxWidth(.2f)
                            .verticalScroll(stateVertical)
                    ) {
                        TextField(
                            userSearch,
                            modifier = Modifier.fillMaxWidth(),
                            onValueChange = { userSearch = it },
                            label = { Text("Search User") })
                        userService.getAllUsers().map {
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
                                selectedUser = it
                            }) {
                                Text(it.name, fontSize = if (inside) 25.sp else 20.sp)
                            }
                            Divider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
                        }
                    }
                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(stateVertical)
                    )
                    val stateVerticalContent = rememberScrollState(0)
                    BoxWithConstraints {  }
                    Column(modifier = Modifier.background(Color.Red).width(500.dp).fillMaxHeight().verticalScroll(stateVerticalContent)) {
                        selectedUser?.let {
                            messageService.messagesFor(it).forEach {
                                val arrangement = if (it.sender == Sender.Self) Arrangement.End else Arrangement.Start
                                Row(modifier = Modifier.fillMaxWidth().padding(10.dp), horizontalArrangement = arrangement) {
                                    Card(shape = RoundedCornerShape(10.dp), elevation = 10.dp) {
                                        ListItem(
                                            modifier = Modifier.fillMaxWidth(.55f)
                                                .background(Color.Cyan),
                                            trailing = {
                                                Text(
                                                    it.date.toJavaLocalDateTime().toLocalTime()
                                                        .format(DateTimeFormatter.ofPattern("HH:mm"))
                                                )
                                            }
                                        ) {
                                            Text(it.content)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    VerticalScrollbar(
                        adapter = rememberScrollbarAdapter(stateVerticalContent)
                    )
                }
            }
        }
    }
}


/**
 * TODO create own cool text bubble shape
 */
private val TriangleShape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)

    lineTo(size.width, size.height)

    lineTo(0f, size.height)
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
