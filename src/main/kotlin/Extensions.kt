import androidx.compose.runtime.Composable

@Composable
fun <T> List<T>.intersperse(item: @Composable () -> T): List<T> = intersperse(item())

@Composable
fun <T> List<T>.intersperse(item: T): List<T> {
    val result = mutableListOf<T>()
    for (element in this) {
        result.add(element)
        result.add(item)
    }
    result.removeAt(result.size - 1)
    return result
}

