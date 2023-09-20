package services

import Instances
import Users
import chat.commons.routing.ReceiverPayload
import chat.commons.routing.ReceiverPayloadLogin
import chat.commons.routing.ReceiverPayloadRegister
import chat.commons.routing.ReceiverPayloadWithId
import createStore
import installAuth
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import logger.LoggerDelegate
import model.CurrentUser
import model.OnlineUser
import resetApplication
import store

class UserServiceImpl : UserService {
    private val logger by LoggerDelegate()

    override suspend fun getAllUsers(): List<OnlineUser> {
        val response: List<ReceiverPayload> = Instances.httpClient.get(Users.Registered()).body()

        return response.map {
            OnlineUser(name = it.name, icon = "")
        }
    }

    override fun isNameTaken(name: String): Boolean {
        return false;
    }

    override suspend fun login(name: String) {
        val body: ReceiverPayloadWithId = Instances.httpClient.post(Users.Login()) {
            setBody(ReceiverPayloadLogin(name))
        }.body()

        val user = CurrentUser(id = body.id, name = name, icon = Any())
        store = CoroutineScope(SupervisorJob()).createStore(user)
        getAllUsers().forEach {
            Instances.cacheService.load(it)
        }
        Instances.httpClient.installAuth(user.name, user.id.toString())
    }

    override suspend fun register(name: String) {
        val body: ReceiverPayloadWithId = Instances.httpClient.post(Users.Register()) {
            setBody(ReceiverPayloadRegister(name))
        }.body()

        val user = CurrentUser(id = body.id, name = name, icon = Any())
        store = CoroutineScope(SupervisorJob()).createStore(user)
        getAllUsers().forEach {
            Instances.cacheService.load(it)
        }
        Instances.httpClient.installAuth(user.name, user.id.toString())
    }

    override suspend fun logout() {
        Instances.cacheService.cache()
        resetApplication()
//        store.currentUser?.let {
//            Instances.httpClient.post(Users.Logout()) {
//                setBody(ReceiverPayloadLogout(it.id, it.name)) // TODO
//            }
//        }
    }
}