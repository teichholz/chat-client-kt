package services

import createStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import model.CurrentUser
import model.OnlineUser
import store

class UserServiceMock : UserService {
    override suspend fun getAllUsers(): List<OnlineUser> {
        return (0..40).map {
            OnlineUser(name = "User $it", icon = Any())
        }
    }

    override fun isNameTaken(name: String): Boolean {
        return false
    }

    override suspend fun login(name: String) {
        val user = CurrentUser(id = 0, name = name, icon = Any())
        store = CoroutineScope(SupervisorJob()).createStore(user)
    }

    override suspend fun register(name: String) {
        val user = CurrentUser(id = 0, name = name, icon = Any())
        store = CoroutineScope(SupervisorJob()).createStore(user)
    }
}