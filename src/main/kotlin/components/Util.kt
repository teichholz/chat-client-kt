package components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import model.Sender


@Composable
fun withVerticalScroll(
    modifier: Modifier = Modifier.fillMaxHeight(1f),
    content: @Composable (scrollState: ScrollState) -> Unit
) {
    val stateVerticalContent = rememberScrollState(0)
    Row(modifier = modifier) {
        content(stateVerticalContent)
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(stateVerticalContent)
        )
    }
}

@Composable
fun ChatMessage(sender: Sender, content: @Composable () -> Unit) {
    val bottomStart = if (sender == Sender.Self) 0.dp else 10.dp
    val bottomEnd = if (sender == Sender.Other) 0.dp else 10.dp
    val card = @Composable {
        Card(
            shape = RoundedCornerShape(10.dp, 10.dp, bottomStart, bottomEnd),
            elevation = 0.dp
        ) { content() }
    }
    val triangle = @Composable { Triangle(risingToTheRight = sender == Sender.Other, background = Color.White) }

    Row(verticalAlignment = Bottom) {
        if (sender == Sender.Self) {
            card()
            triangle()
        } else {
            triangle()
            card()
        }
    }
}

@Composable
fun Triangle(risingToTheRight: Boolean, background: Color) {
    Box(
        Modifier
            .padding(bottom = 0.dp, start = 0.dp)
            .clip(TriangleEdgeShape(risingToTheRight))
            .background(background)
            .size(7.dp)
    )
}

class TriangleEdgeShape(val risingToTheRight: Boolean) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // (0,0) is the top left corner of the layout, just like in video game engines
        val trianglePath = if (risingToTheRight) {
            Path().apply {
                moveTo(x = 0f, y = size.height)
                lineTo(x = size.width, y = 0f)
                lineTo(x = size.width, y = size.height)
            }
        } else {
            Path().apply {
                moveTo(x = 0f, y = 0f)
                lineTo(x = size.width, y = size.height)
                lineTo(x = 0f, y = size.height)
            }
        }

        return Outline.Generic(path = trianglePath)
    }
}
