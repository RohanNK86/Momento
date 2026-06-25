package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.UserProfile
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(viewModel: MomentoViewModel, onNavigateBack: () -> Unit) {
    val profile by viewModel.userProfile.collectAsStateWithLifecycle()
    var isEditing by remember { mutableStateOf(false) }

    // Editable state
    var name by remember(profile) { mutableStateOf(profile?.name ?: "Rohan") }
    var gender by remember(profile) { mutableStateOf(profile?.gender ?: "") }
    var occupation by remember(profile) { mutableStateOf(profile?.occupation ?: "") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "") }
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var bio by remember(profile) { mutableStateOf(profile?.bio ?: "") }
    var goalsText by remember(profile) { mutableStateOf(profile?.goalsText ?: "") }
    var weightStr by remember(profile) { mutableStateOf(profile?.weightKg?.toString() ?: "") }
    var heightStr by remember(profile) { mutableStateOf(profile?.heightCm?.toString() ?: "") }
    var dobMillis by remember(profile) { mutableStateOf(profile?.dateOfBirth) }
    var showDobPicker by remember { mutableStateOf(false) }

    val initials = viewModel.getInitials(name)
    val avatarColor = try { Color(android.graphics.Color.parseColor(profile?.avatarColor ?: "#6366F1")) } catch (e: Exception) { MomentoPrimary }

    // Age calc
    val age = dobMillis?.let {
        val dob = Calendar.getInstance().apply { timeInMillis = it }
        val now = Calendar.getInstance()
        var a = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) a--
        a
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top app bar
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.Transparent).padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = MomentoOnSurface)
            }
            Text("Profile", style = MaterialTheme.typography.titleLarge, color = MomentoOnSurface, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = {
                if (isEditing) {
                    viewModel.saveProfile(
                        UserProfile(
                            name = name.ifBlank { "Rohan" },
                            gender = gender, occupation = occupation, email = email,
                            phone = phone, bio = bio, goalsText = goalsText,
                            weightKg = weightStr.toFloatOrNull(),
                            heightCm = heightStr.toFloatOrNull(),
                            dateOfBirth = dobMillis
                        )
                    )
                }
                isEditing = !isEditing
            }) {
                Text(if (isEditing) "Save" else "Edit", color = MomentoPrimary, fontWeight = FontWeight.Bold)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            item {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier.size(100.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(avatarColor, MomentoSecondary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(name, style = MaterialTheme.typography.headlineSmall, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                    if (occupation.isNotBlank()) {
                        Text(occupation, color = MomentoOnSurfaceVariant, fontSize = 14.sp)
                    }
                }
            }

            // Personal Info card
            item {
                ProfileCard("Personal Information") {
                    ProfileField("Full Name", name, Icons.Default.Person, isEditing) { name = it }
                    ProfileField("Gender", gender, Icons.Default.Wc, isEditing) { gender = it }
                    ProfileField("Occupation", occupation, Icons.Default.Work, isEditing) { occupation = it }
                    // DOB row
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Cake, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Date of Birth", color = MomentoOnSurfaceVariant, fontSize = 11.sp)
                            val dobStr = dobMillis?.let { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it)) } ?: "Not set"
                            if (isEditing) {
                                TextButton(onClick = { showDobPicker = true }, contentPadding = PaddingValues(0.dp)) {
                                    Text(dobStr, color = MomentoPrimary)
                                }
                            } else {
                                Text("$dobStr${age?.let { " (Age $it)" } ?: ""}", color = MomentoOnSurface, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Contact info
            item {
                ProfileCard("Contact") {
                    ProfileField("Email", email, Icons.Default.Email, isEditing) { email = it }
                    ProfileField("Phone", phone, Icons.Default.Phone, isEditing) { phone = it }
                }
            }

            // Health
            item {
                ProfileCard("Health") {
                    ProfileFieldNumeric("Weight (kg)", weightStr, Icons.Default.FitnessCenter, isEditing) { weightStr = it }
                    ProfileFieldNumeric("Height (cm)", heightStr, Icons.Default.Height, isEditing) { heightStr = it }
                }
            }

            // Bio & Goals
            item {
                ProfileCard("About Me") {
                    ProfileField("Bio", bio, Icons.Default.Info, isEditing) { bio = it }
                    ProfileField("Goals", goalsText, Icons.Default.EmojiEvents, isEditing) { goalsText = it }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Simple DOB date picker dialog
    if (showDobPicker) {
        var yearStr by remember { mutableStateOf(dobMillis?.let { Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.YEAR).toString() } ?: "2000") }
        var monthStr by remember { mutableStateOf(dobMillis?.let { (Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.MONTH) + 1).toString() } ?: "1") }
        var dayStr by remember { mutableStateOf(dobMillis?.let { Calendar.getInstance().apply { timeInMillis = it }.get(Calendar.DAY_OF_MONTH).toString() } ?: "1") }
        Dialog(onDismissRequest = { showDobPicker = false }) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MomentoSurfaceContainerHigh)) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Date of Birth", style = MaterialTheme.typography.titleMedium, color = MomentoOnSurface)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = dayStr, onValueChange = { dayStr = it }, label = { Text("Day") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = momentoTextFieldColors())
                        OutlinedTextField(value = monthStr, onValueChange = { monthStr = it }, label = { Text("Month") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = momentoTextFieldColors())
                        OutlinedTextField(value = yearStr, onValueChange = { yearStr = it }, label = { Text("Year") }, modifier = Modifier.weight(2f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = momentoTextFieldColors())
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showDobPicker = false }) { Text("Cancel", color = MomentoOnSurfaceVariant) }
                        Button(onClick = {
                            try {
                                val cal = Calendar.getInstance().apply {
                                    set(Calendar.YEAR, yearStr.toInt())
                                    set(Calendar.MONTH, monthStr.toInt() - 1)
                                    set(Calendar.DAY_OF_MONTH, dayStr.toInt())
                                }
                                dobMillis = cal.timeInMillis
                            } catch (e: Exception) { /* ignore invalid */ }
                            showDobPicker = false
                        }, colors = ButtonDefaults.buttonColors(containerColor = MomentoPrimary)) { Text("Set") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(title, color = MomentoPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ProfileField(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        if (isEditing) {
            OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label, color = MomentoOnSurfaceVariant, fontSize = 11.sp) }, modifier = Modifier.weight(1f), colors = momentoTextFieldColors(), singleLine = true)
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = MomentoOnSurfaceVariant, fontSize = 11.sp)
                Text(value.ifBlank { "—" }, color = MomentoOnSurface, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun ProfileFieldNumeric(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isEditing: Boolean, onValueChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        if (isEditing) {
            OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label, color = MomentoOnSurfaceVariant, fontSize = 11.sp) }, modifier = Modifier.weight(1f), colors = momentoTextFieldColors(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = MomentoOnSurfaceVariant, fontSize = 11.sp)
                Text(value.ifBlank { "—" }, color = MomentoOnSurface, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun momentoTextFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color.White.copy(alpha = 0.05f),
    unfocusedContainerColor = Color.White.copy(alpha = 0.03f),
    focusedTextColor = Color(0xFFF1F5F9),
    unfocusedTextColor = Color(0xFFF1F5F9),
    focusedIndicatorColor = Color(0xFF6366F1),
    unfocusedIndicatorColor = Color.White.copy(alpha = 0.15f),
    cursorColor = Color(0xFF6366F1)
)
