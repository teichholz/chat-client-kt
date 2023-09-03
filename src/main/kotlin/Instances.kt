import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import services.MessageService
import services.UserService

object Instances {
    val userService = UserService()
    val messageService = MessageService()

    val httpClient = HttpClient(CIO) {
        install(WebSockets)
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