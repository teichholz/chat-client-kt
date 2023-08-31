package services

import Instances
import Users
import chat.commons.routing.ReceiverPayloadLogout
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.isActive
import logger.LoggerDelegate

class UserService {
    val logger by LoggerDelegate()

    val client = Instances.httpClient

    fun getAllUsers(): List<User> {
        return (0..40).map {
            User("User $it", Any())
        }
    }

    fun isNameTaken(email: String): Boolean {
        return false;
    }

    suspend fun login() {
        client.webSocket("127.0.0.1:8080/login") {
            val response: String =  call.body()
            logger.info("Login: $response")

            while (isActive) {
                val othersMessage = incoming.receive() as? Frame.Text
                println(othersMessage?.readText())
            }
        }
    }

    suspend fun logout() {
        val body: ReceiverPayloadLogout =  client.get(Users.Logout()).body()
    }
}