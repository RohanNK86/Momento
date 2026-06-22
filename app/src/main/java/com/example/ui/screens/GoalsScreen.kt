package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Goal
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel

@Composable
fun GoalsScreen(viewModel: MomentoViewModel) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Column {
                Text("My Goals", style = MaterialTheme.typography.displayLarge, color = MomentoOnSurface)
                Text("Stay focused and track your progress.", style = MaterialTheme.typography.bodyLarge, color = MomentoOnSurfaceVariant)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(1f)) {
            items(goals.filter { it.status != Goal.GoalStatus.COMPLETED }, key = { it.id }) { goal ->
                GoalCard(goal)
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MomentoTertiary)
                    Text("Recently Achieved", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(goals.filter { it.status == Goal.GoalStatus.COMPLETED }, key = { it.id }) { goal ->
                AchievedGoalItem(goal)
            }
        }
    }
}

@Composable
fun GoalCard(goal: Goal) {
    val color = when (goal.status) {
        Goal.GoalStatus.ON_TRACK -> MomentoTertiary
        Goal.GoalStatus.AT_RISK -> MomentoError
        Goal.GoalStatus.COMPLETED -> MomentoPrimary
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Column {
            Text(goal.status.name.replace("_", " "), fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold, modifier = Modifier
                .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(goal.title, style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
            Text("Target: Date Placeholder", style = MaterialTheme.typography.bodyMedium, color = MomentoOnSurfaceVariant)
            
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                    CircularProgressIndicator(progress = { goal.progress }, modifier = Modifier.fillMaxSize(), color = color, strokeWidth = 6.dp, trackColor = Color.White.copy(alpha = 0.05f))
                    Text("${(goal.progress * 100).toInt()}%", style = MaterialTheme.typography.headlineMedium, color = MomentoOnSurface)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("12", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MomentoOnSurface)
                    Text("Days Left", style = MaterialTheme.typography.labelSmall, color = MomentoOnSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun AchievedGoalItem(goal: Goal) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MomentoTertiary.copy(alpha = 0.1f))
                    .border(1.dp, MomentoTertiary.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MomentoTertiary)
            }
            Column {
                Text(goal.title, style = MaterialTheme.typography.bodyLarge, color = MomentoOnSurface)
                Text("Completed", style = MaterialTheme.typography.bodyMedium, color = MomentoOnSurfaceVariant)
            }
        }
        Text("100%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MomentoTertiary)
    }
}
