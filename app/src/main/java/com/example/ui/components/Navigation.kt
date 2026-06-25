package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class NavItem(val route: String, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    NavItem("home",     "Home",    Icons.Default.Home),
    NavItem("tasks",    "Tasks",   Icons.Default.TaskAlt),
    NavItem("habits",   "Habits",  Icons.Default.Loop),
    NavItem("events",   "Events",  Icons.Default.CalendarMonth),
    NavItem("expenses", "Spend",   Icons.Default.Payments)
)

@Composable
fun BottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, MomentoBackground.copy(alpha = 0.95f)),
                    startY = 0f, endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.06f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                BottomNavItem(item = item, isSelected = currentRoute == item.route) {
                    onNavigate(item.route)
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(item: NavItem, isSelected: Boolean, onClick: () -> Unit) {
    val color by animateColorAsState(
        targetValue = if (isSelected) MomentoPrimary else Color.Gray,
        animationSpec = tween(200), label = "navColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy), label = "navScale"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                if (isSelected) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(MomentoPrimary.copy(alpha = 0.15f)))
                }
                Icon(
                    item.icon, contentDescription = item.label, tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(item.label, color = color, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

/** Profile avatar button for the top-right corner of screens. */
@Composable
fun ProfileAvatarButton(initials: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(MomentoPrimary, MomentoSecondary)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(initials.take(2), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

/** Global top app bar used on main screens. */
@Composable
fun MomentoTopBar(title: String, profileInitials: String, onProfileClick: () -> Unit, onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        IconButton(onClick = onSearchClick) {
            Icon(Icons.Default.Search, "Search", tint = MomentoOnSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(4.dp))
        ProfileAvatarButton(initials = profileInitials, onClick = onProfileClick)
    }
}
