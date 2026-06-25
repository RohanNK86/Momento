package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MomentoOutline

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.05f,
    cornerRadius: Dp = 20.dp,
    borderAlpha: Float = 0.1f,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cornerRadius))
            .background(Color.White.copy(alpha = alpha))
            .border(1.dp, Color.White.copy(alpha = borderAlpha), RoundedCornerShape(cornerRadius))
            .animateContentSize()
            .padding(16.dp),
        content = content
    )
}
