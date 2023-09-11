import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import services.MessageService
import services.MessageServiceMock
import services.UserService
import services.UserServiceMock

val userServiceFactory = { UserServiceMock() }
val messageServiceFactory = { MessageServiceMock() }
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
            json()
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

fun reset() {
    Instances.userService = userServiceFactory()
    Instances.messageService = messageServiceFactory()
    Instances.httpClient = httpClientFactory()
}

fun HttpClient.installAuth(username: String, password: String): HttpClient {
    return config {
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username, password)
                }
                realm = "Access to the '/' path"
            }
        }
    }
}