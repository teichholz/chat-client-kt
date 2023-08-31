import io.ktor.resources.*

@Resource("/users")
class Users {
    @Resource("/login")
    class Login(val parent: Users = Users()) {}
    @Resource("/register")
    class Register(val parent: Users = Users()) {}
    @Resource("/logout")
    class Logout(val parent: Users = Users()) {}
}