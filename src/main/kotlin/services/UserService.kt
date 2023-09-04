package services

import Action
import Instances
import Users
import chat.commons.routing.ReceiverPayloadLogin
import chat.commons.routing.ReceiverPayloadLogout
import chat.commons.routing.ReceiverPayloadRegister
import chat.commons.routing.ReceiverPayloadWithId
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.isActive
import logger.LoggerDelegate
import store

class UserService {
    val logger by LoggerDelegate()

    fun getAllUsers(): List<OnlineUser> {
        return (0..40).map {
            OnlineUser(name = "User $it", icon = Any())
        }
    }

    fun isNameTaken(name: String): Boolean {
        return false;
    }

    suspend fun auth() {
        Instances.httpClient.webSocket("127.0.0.1:8080/users/register") {
            val response: String =  call.body()
            logger.info("Register: $response")

            while (isActive) {
                val othersMessage = incoming.receive() as? Frame.Text
                println(othersMessage?.readText())
            }
        }
    }

    suspend fun login(name: String) {
        val body: ReceiverPayloadWithId = Instances.httpClient.post(Users.Login()) {
            setBody(ReceiverPayloadLogin(name))
        }.body()

        val user = CurrentUser(id = body.id, name = name, icon = Any())
        store.send(Action.Login(user))
        logger.info("Logged in as $user")
    }

    suspend fun register(name: String) {
        val body: ReceiverPayloadWithId = Instances.httpClient.post(Users.Register()) {
            setBody(ReceiverPayloadRegister(name))
        }.body()

        val user = CurrentUser(id = body.id, name = name, icon = Any())
        store.send(Action.Login(user))
        logger.info("Registered as $user")
    }

    suspend fun logout() {
        store.currentUser?.let {
            Instances.httpClient.post(Users.Logout()) {
                setBody(ReceiverPayloadLogout(it.id, it.name)) // TODO
            }
        }
    }
}