package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Note
import com.example.ui.components.AddNoteDialog
import com.example.ui.theme.*
import com.example.viewmodel.MomentoViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotesScreen(viewModel: MomentoViewModel) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf<Note?>(null) }

    if (showAddDialog) {
        AddNoteDialog(onDismiss = { showAddDialog = false }) { title, content, tags ->
            viewModel.addNote(title, content, tags)
        }
    }
    editingNote?.let { note ->
        AddNoteDialog(
            initialTitle = note.title,
            initialContent = note.content,
            initialTags = note.tags,
            onDismiss = { editingNote = null }
        ) { title, content, tags ->
            viewModel.updateNote(note, title, content)
        }
    }

    val filtered = notes.filter {
        searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true) || it.content.contains(searchQuery, ignoreCase = true) || it.tags.contains(searchQuery, ignoreCase = true)
    }
    val pinned = filtered.filter { it.isPinned }
    val unpinned = filtered.filter { !it.isPinned }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text("Notes", style = MaterialTheme.typography.displaySmall, color = MomentoOnSurface, fontWeight = FontWeight.Bold)
                Text("${notes.size} notes saved", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
            }
            FloatingActionButton(
                onClick = { showAddDialog = true }, modifier = Modifier.size(48.dp),
                containerColor = Color.Transparent, shape = RoundedCornerShape(14.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(MomentoSecondary, MomentoPrimary)), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Search bar
        OutlinedTextField(
            value = searchQuery, onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
            placeholder = { Text("Search notes…", color = MomentoOnSurfaceVariant) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = MomentoOnSurfaceVariant) },
            trailingIcon = { if (searchQuery.isNotEmpty()) IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, null, tint = MomentoOnSurfaceVariant) } },
            colors = momentoTextFieldColors(), shape = RoundedCornerShape(12.dp), singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Notes, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No notes yet", color = MomentoOnSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                    Text("Tap + to capture your thoughts", color = MomentoOnSurfaceVariant, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                if (pinned.isNotEmpty()) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.PushPin, null, tint = MomentoTertiary, modifier = Modifier.size(14.dp))
                            Text("PINNED", color = MomentoTertiary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                    items(pinned, key = { it.id }) { note ->
                        NoteCard(note, viewModel) { editingNote = note }
                    }
                    if (unpinned.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("OTHER NOTES", color = MomentoOnSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
                items(unpinned, key = { it.id }) { note ->
                    NoteCard(note, viewModel) { editingNote = note }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun NoteCard(note: Note, viewModel: MomentoViewModel, onEdit: () -> Unit) {
    val noteColor = try { Color(android.graphics.Color.parseColor(note.color)) } catch (e: Exception) { MomentoSurfaceContainer }
    val dateFmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, if (note.isPinned) MomentoTertiary.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.07f), RoundedCornerShape(18.dp))
            .clickable(onClick = onEdit)
            .padding(16.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(note.title, color = MomentoOnSurface, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (note.isFavorite) Icon(Icons.Default.Favorite, null, tint = MomentoError, modifier = Modifier.size(14.dp).padding(start = 4.dp))
                if (note.isPinned) Icon(Icons.Default.PushPin, null, tint = MomentoTertiary, modifier = Modifier.size(14.dp))
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.MoreVert, null, tint = MomentoOnSurfaceVariant, modifier = Modifier.size(16.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }, modifier = Modifier.background(MomentoSurfaceContainerHigh)) {
                        DropdownMenuItem(text = { Text(if (note.isPinned) "Unpin" else "Pin", color = MomentoOnSurface) }, onClick = { viewModel.toggleNotePin(note); showMenu = false }, leadingIcon = { Icon(Icons.Default.PushPin, null, tint = MomentoTertiary) })
                        DropdownMenuItem(text = { Text(if (note.isFavorite) "Unfavorite" else "Favorite", color = MomentoOnSurface) }, onClick = { viewModel.toggleNoteFavorite(note); showMenu = false }, leadingIcon = { Icon(Icons.Default.Favorite, null, tint = MomentoError) })
                        DropdownMenuItem(text = { Text("Edit", color = MomentoOnSurface) }, onClick = { onEdit(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Edit, null, tint = MomentoOnSurface) })
                        DropdownMenuItem(text = { Text("Delete", color = MomentoError) }, onClick = { viewModel.deleteNote(note); showMenu = false }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = MomentoError) })
                    }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(note.content, color = MomentoOnSurfaceVariant, fontSize = 13.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            if (note.tags.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    note.tags.split(",").take(3).forEach { tag ->
                        Text(
                            "#${tag.trim()}", color = MomentoPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(MomentoPrimary.copy(alpha = 0.1f)).padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(dateFmt.format(Date(note.updatedAt)), color = MomentoOnSurfaceVariant, fontSize = 10.sp)
        }
    }
}
