package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.1f,
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier.clip(RoundedCornerShape(32.dp)),
        color = Color.White.copy(alpha = alpha),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = 16.dp
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            content()
        }
    }
}
