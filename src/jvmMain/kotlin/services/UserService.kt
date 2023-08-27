package services

class UserService {
    fun getAllUsers(): List<User> {
        return (0..40).map {
            User("User $it", Any())
        }
    }
}