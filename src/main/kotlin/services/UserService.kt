package services

import createStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import model.CurrentUser
import model.OnlineUser
import store

interface UserService {
    suspend fun getAllUsers(): List<OnlineUser>

    fun isNameTaken(name: String): Boolean

    suspend fun auth(user: CurrentUser) {
        store = CoroutineScope(SupervisorJob()).createStore(user)
    }

    suspend fun login(name: String)
    suspend fun register(name: String)
    suspend fun logout() {}
}