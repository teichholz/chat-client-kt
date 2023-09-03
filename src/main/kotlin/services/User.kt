package services

/**
 * @param id comes from the server
 */
data class CurrentUser(var id: Int, val name: String, val icon: Any) {}

data class OnlineUser(val name: String, val icon: Any) {}
