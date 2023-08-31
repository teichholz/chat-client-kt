import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import services.MessageService
import services.UserService

object Instances {
    val userService = UserService()
    val messageService = MessageService()

    val httpClient = HttpClient(CIO) {
        install(WebSockets)
        install(Resources)
        defaultRequest {
            host = "localhost"
            port = 8080
            url {
                protocol = URLProtocol.HTTP
            }
        }
    }
}