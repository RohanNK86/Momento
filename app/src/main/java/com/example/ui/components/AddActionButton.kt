package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MomentoOnSurfaceVariant
import com.example.ui.theme.MomentoPrimary
import com.example.ui.theme.MomentoPrimaryContainer

@Composable
fun AddActionButton(onClick: () -> Unit, description: String = "Add") {
    val shape = RoundedCornerShape(14.dp)
    FilledTonalIconButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp).clip(shape)
            .border(1.dp, MomentoOnSurfaceVariant.copy(alpha = 0.16f), shape)
            .semantics { contentDescription = description },
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MomentoPrimaryContainer,
            contentColor = MomentoPrimary
        )
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
    }
}

