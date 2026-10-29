package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(900)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MomentoBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(76.dp).clip(RoundedCornerShape(24.dp)).background(MomentoPrimaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MomentoPrimary, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(22.dp))
            Text("Momento", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Make today count.", fontSize = 14.sp, color = MomentoOnSurfaceVariant)
            Spacer(modifier = Modifier.height(28.dp))
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MomentoPrimary)
        }
    }
}

