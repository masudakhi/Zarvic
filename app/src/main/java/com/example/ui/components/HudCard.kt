package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceVariant

@Composable
fun HudCard(
    modifier: Modifier = Modifier,
    borderColor: Color = JarvisSurfaceBorder,
    glowCyan: Boolean = false,
    cornerRadius: Dp = 12.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val activeBorder = if (glowCyan) JarvisCyan.copy(alpha = 0.6f) else borderColor

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                BorderStroke(1.dp, activeBorder),
                RoundedCornerShape(cornerRadius)
            ),
        color = JarvisSurface,
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            JarvisSurfaceVariant.copy(alpha = 0.4f),
                            JarvisSurface
                        )
                    )
                )
                .padding(14.dp),
            content = content
        )
    }
}
