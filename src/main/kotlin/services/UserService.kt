package services

import model.OnlineUser

interface UserService {
    suspend fun getAllUsers(): List<OnlineUser>

    fun isNameTaken(name: String): Boolean

    suspend fun login(name: String)
    suspend fun register(name: String)
    suspend fun logout() {}

}