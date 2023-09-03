package components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun withVerticalScroll(
    modifier: Modifier = Modifier,
    content: @Composable (scrollState: ScrollState) -> Unit
) {
    val stateVerticalContent = rememberScrollState(0)
    content(stateVerticalContent)
    VerticalScrollbar(
        adapter = rememberScrollbarAdapter(stateVerticalContent),
        modifier = modifier
    )
}

/**
 * TODO create own cool text bubble shape
 */
private val TriangleShape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)

    lineTo(size.width, size.height)

    lineTo(0f, size.height)
}
