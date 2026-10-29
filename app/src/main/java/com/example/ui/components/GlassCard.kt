package com.example.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MomentoOutline
import com.example.ui.theme.MomentoSurfaceBase

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
            .shadow(3.dp, RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .background(MomentoSurfaceBase.copy(alpha = 0.9f))
            .border(1.dp, MomentoOutline.copy(alpha = 0.8f), RoundedCornerShape(cornerRadius))
            .animateContentSize()
            .padding(16.dp),
        content = content
    )
}

