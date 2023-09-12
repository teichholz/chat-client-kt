import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import services.MessageService
import services.MessageServiceImpl
import services.MessageServiceMock
import services.UserService
import services.UserServiceImpl
import services.UserServiceMock

val prod = true

val userServiceFactory = { if (prod) UserServiceImpl() else UserServiceMock() }
val messageServiceFactory = { if (prod) MessageServiceImpl() else MessageServiceMock() }
// TODO Snackbar for http errors
val httpClientFactory = {
    HttpClient(CIO) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Resources)
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            headers {
                contentType(ContentType.Application.Json)
            }
            host = "127.0.0.1"
            port = 8080
            url {
                protocol = URLProtocol.HTTP
            }
        }
    }
}

object Instances {
    var userService: UserService = userServiceFactory()
    var messageService: MessageService = messageServiceFactory()

    var httpClient: HttpClient = httpClientFactory()
}

fun resetApplication() {
    store = EmptyStore
    user = null
    password = null
    Instances.userService = userServiceFactory()
    Instances.messageService = messageServiceFactory()
    Instances.httpClient = httpClientFactory()
}

var user: String? = null
var password: String? = null
fun HttpClient.installAuth(username: String, pass: String) {
    user = username
    password = pass
    Instances.httpClient = config {
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username, pass)
                }
                realm = "Access to the '/' path"
            }
        }
    }
}

/**
 * Seems weird that I have to do this, but I have to :(
 */
suspend fun HttpClient.webSocketAuth(endpoint: String, block: suspend DefaultClientWebSocketSession.() -> Unit) {
    webSocket("ws://127.0.0.1:8080/$endpoint", {
        val user = user ?: throw IllegalStateException("User not set")
        val password = password ?: throw IllegalStateException("Password not set")
        val basic = "$user:$password".encodeBase64()
        header(HttpHeaders.Authorization, "Basic $basic")
    }) {
        block()
    }
}
