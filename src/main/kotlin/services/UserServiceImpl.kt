package services

import Action
import Instances
import Users
import chat.commons.protocol.AuthPayloadSocket
import chat.commons.protocol.Protocol
import chat.commons.routing.ReceiverPayload
import chat.commons.routing.ReceiverPayloadLogin
import chat.commons.routing.ReceiverPayloadLogout
import chat.commons.routing.ReceiverPayloadRegister
import chat.commons.routing.ReceiverPayloadWithId
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import logger.LoggerDelegate
import model.CurrentUser
import model.OnlineUser
import store

class UserServiceImpl : UserService {
    val logger by LoggerDelegate()

    override suspend fun getAllUsers(): List<OnlineUser> {
        val response: List<ReceiverPayload> = Instances.httpClient.get(Users.Registered()).body()

        return response.map {
            OnlineUser(name = it.name, icon = Any())
        }
    }

    override fun isNameTaken(name: String): Boolean {
        return false;
    }


    /**
     * Probably needs LaunchedEffect to initiate
     */
    override suspend fun auth() {
        if (store.currentUser == null) {
            throw IllegalStateException("User must be logged in")
        }

        val user = store.currentUser!!

        Instances.httpClient.webSocket("127.0.0.1:8080/users/register") {
            val auth = Protocol.AUTH().apply {
                payload = AuthPayloadSocket(lastMessage = 0, receiver = ReceiverPayloadWithId(id = user.id, name = user.name))
            }

            sendSerialized(auth)
            receiveDeserialized<Protocol<*>>()

            Instances.messageService.sendJob()
            Instances.messageService.receiveJob()
        }
    }

    override suspend fun login(name: String) {
        val body: ReceiverPayloadWithId = Instances.httpClient.post(Users.Login()) {
            setBody(ReceiverPayloadLogin(name))
        }.body()

        val user = CurrentUser(id = body.id, name = name, icon = Any())
        store.send(Action.Login(user))
        logger.info("Logged in as $user")
        auth()
    }

    override suspend fun register(name: String) {
        val body: ReceiverPayloadWithId = Instances.httpClient.post(Users.Register()) {
            setBody(ReceiverPayloadRegister(name))
        }.body()

        val user = CurrentUser(id = body.id, name = name, icon = Any())
        store.send(Action.Login(user))
        logger.info("Registered as $user")
        auth()
    }

    override suspend fun logout() {
        store.currentUser?.let {
            Instances.httpClient.post(Users.Logout()) {
                setBody(ReceiverPayloadLogout(it.id, it.name)) // TODO
            }
        }
    }
}