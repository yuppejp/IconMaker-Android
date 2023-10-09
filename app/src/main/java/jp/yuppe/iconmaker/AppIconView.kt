package jp.yuppe.iconmaker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppIconView(iconVector: ImageVector, baseColor: Color, modifier: Modifier = Modifier) {
    val endColor = baseColor.copy(alpha = 0.7f)

    BoxWithConstraints(
        modifier = modifier
    ) {
        val boxWith = constraints.maxWidth.toFloat()
        val boxHeight = constraints.maxHeight.toFloat()

        val gradientBrush = Brush.linearGradient(
            colors = listOf(baseColor, endColor),
            start = Offset(boxWith / 2, boxHeight / 2),
            end = Offset(boxWith / 2, 0f))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.fillMaxSize(0.4f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconViewPreview() {
    Column {
        AppIconView(
            iconVector = Icons.Default.Draw,
            baseColor = Color(235, 149, 0),
            modifier = Modifier.size(200.dp).padding(8.dp)
        )
        AppIconView(
            iconVector = Icons.Default.Train,
            baseColor = Color(101, 196, 102),
            modifier = Modifier.size(200.dp).padding(8.dp)
        )
        AppIconView(
            iconVector = Icons.Default.Sailing,
            baseColor = Color(52, 120, 246),
            modifier = Modifier.size(200.dp).padding(8.dp)
        )
    }
}
