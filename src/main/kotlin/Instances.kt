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
import services.CacheServiceImpl
import services.MessageService
import services.MessageServiceImpl
import services.MessageServiceMock
import services.UserService
import services.UserServiceImpl
import services.UserServiceMock

val prod = false

val userServiceFactory by lazy { if (prod) UserServiceImpl() else UserServiceMock() }
val messageServiceFactory by lazy { if (prod) MessageServiceImpl() else MessageServiceMock() }
val cacheServiceFactory by lazy { CacheServiceImpl(store) }
val httpClientFactory by lazy {
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
        expectSuccess = true
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, request ->
                val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                val exceptionResponse = clientException.response
                val status = exceptionResponse.status
                snackbar.showSnackbar("$status Http Error: $exceptionResponse")
            }
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
    var userService: UserService = userServiceFactory
    var messageService: MessageService = messageServiceFactory
    var cacheService: CacheServiceImpl = cacheServiceFactory
    var httpClient: HttpClient = httpClientFactory
}

fun resetApplication() {
    store = EmptyStore
    Instances.userService = userServiceFactory
    Instances.messageService = messageServiceFactory
    Instances.cacheService = cacheServiceFactory
    Instances.httpClient = httpClientFactory
}

fun HttpClient.installAuth(username: String, pass: String) {
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
        val user = store.currentUser.name
        val password = store.currentUser.id
        val basic = "$user:$password".encodeBase64()
        header(HttpHeaders.Authorization, "Basic $basic")
    }) {
        block()
    }
}
