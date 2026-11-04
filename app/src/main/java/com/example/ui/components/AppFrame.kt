package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Keeps the UI at a phone-sized width so tablets, high-res previews, and
 * mis-scaled emulators do not stretch the layout into a tiny "zoomed out" look.
 */
@Composable
fun AppFrame(content: @Composable () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val cap = if (maxWidth > 480.dp) 420.dp else maxWidth
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = cap)
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                content()
            }
        }
    }
}
