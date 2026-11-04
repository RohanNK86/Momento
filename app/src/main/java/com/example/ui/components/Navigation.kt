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
            .background(MomentoBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MomentoSurfaceBase)
                .border(1.dp, MomentoOutline, RoundedCornerShape(24.dp))
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                BottomNavItem(modifier = Modifier.weight(1f), item = item, isSelected = currentRoute == item.route) {
                    onNavigate(item.route)
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(modifier: Modifier = Modifier, item: NavItem, isSelected: Boolean, onClick: () -> Unit) {
    val color by animateColorAsState(
        targetValue = if (isSelected) MomentoPrimary else MomentoOnSurfaceVariant,
        animationSpec = tween(200), label = "navColor"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                if (isSelected) {
                    Box(modifier = Modifier.width(46.dp).height(30.dp).clip(RoundedCornerShape(12.dp)).background(MomentoPrimaryContainer))
                }
                Icon(
                    item.icon, contentDescription = item.label, tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(item.label, color = color, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
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
            .background(MomentoPrimaryContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(initials.take(2), color = MomentoOnPrimaryContainer, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

/** Global top app bar used on main screens. */
@Composable
fun MomentoTopBar(title: String, profileInitials: String, onProfileClick: () -> Unit, onSearchClick: () -> Unit, onSettingsClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(12.dp)).background(MomentoPrimary), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        IconButton(onClick = onSearchClick) {
            Icon(Icons.Default.Search, "Search", tint = MomentoOnSurfaceVariant)
        }
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Default.Settings, "Settings", tint = MomentoOnSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(4.dp))
        ProfileAvatarButton(initials = profileInitials, onClick = onProfileClick)
    }
}

