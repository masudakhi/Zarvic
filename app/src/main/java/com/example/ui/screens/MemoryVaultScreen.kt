package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JarvisLanguage
import com.example.model.VaultMemory
import com.example.ui.components.HudCard
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisNeonGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun MemoryVaultScreen(
    memories: List<VaultMemory>,
    language: JarvisLanguage,
    onSaveMemory: (title: String, content: String, category: String) -> Unit,
    onTogglePin: (VaultMemory) -> Unit,
    onDeleteMemory: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    val filtered = memories.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
                it.decryptedContent.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Security Card
            HudCard(
                borderColor = JarvisCyan.copy(alpha = 0.5f),
                glowCyan = true
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(JarvisCyan.copy(alpha = 0.15f))
                            .border(1.dp, JarvisCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "AES-256",
                            tint = JarvisCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (language == JarvisLanguage.BENGALI) "এনক্রিপ্ট করা মেমরি ভল্ট" else "ENCRYPTED MEMORY VAULT",
                            color = JarvisCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (language == JarvisLanguage.BENGALI) {
                                "সম্পূর্ণ লোকাল AES-256 সুরক্ষা • ক্লাউডলেস প্রাইভেসি"
                            } else {
                                "Zero-Cloud AES-256 Encryption • 100% Local Storage"
                            },
                            color = JarvisTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "মেমরি বা তথ্য খুঁজুন…" else "Search encrypted memories…",
                        color = JarvisTextSecondary,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = JarvisCyan
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("vault_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = JarvisTextPrimary,
                    unfocusedTextColor = JarvisTextPrimary,
                    focusedBorderColor = JarvisCyan,
                    unfocusedBorderColor = JarvisSurfaceBorder,
                    focusedContainerColor = JarvisSurface,
                    unfocusedContainerColor = JarvisSurface
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Memory List
            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Empty",
                            tint = JarvisSurfaceBorder,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (language == JarvisLanguage.BENGALI) "কোনো সংরক্ষিত মেমরি নেই" else "No stored memories found",
                            color = JarvisTextSecondary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (language == JarvisLanguage.BENGALI) {
                                "+ বাটনে চাপ দিয়ে গোপন তথ্য এনক্রিপ্ট করে রাখুন"
                            } else {
                                "Tap + to store confidential information locally"
                            },
                            color = JarvisTextSecondary.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered, key = { it.id }) { memory ->
                        VaultMemoryCard(
                            memory = memory,
                            onTogglePin = { onTogglePin(memory) },
                            onDelete = { onDeleteMemory(memory.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add New Memory
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_memory_fab"),
            containerColor = JarvisCyan,
            contentColor = JarvisBackground
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Memory")
        }

        // Add Memory Dialog
        if (showAddDialog) {
            AddMemoryDialog(
                language = language,
                onDismiss = { showAddDialog = false },
                onSave = { title, content, cat ->
                    onSaveMemory(title, content, cat)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun VaultMemoryCard(
    memory: VaultMemory,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    HudCard(
        borderColor = if (memory.isPinned) JarvisCyan else JarvisSurfaceBorder,
        glowCyan = memory.isPinned
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(JarvisSurfaceVariant)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = memory.category.uppercase(),
                            color = if (memory.isPinned) JarvisCyan else JarvisAmber,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = memory.title,
                        color = JarvisTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (memory.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin",
                            tint = if (memory.isPinned) JarvisCyan else JarvisTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = JarvisRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = memory.decryptedContent,
                color = JarvisTextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun AddMemoryDialog(
    language: JarvisLanguage,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("PERSONAL") }

    val categories = listOf("PERSONAL", "WORK", "CODING", "SECRET")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = JarvisSurface,
        title = {
            Text(
                text = if (language == JarvisLanguage.BENGALI) "নতুন মেমরি সংরক্ষণ" else "Save New Memory",
                color = JarvisCyan,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = {
                        Text(
                            text = if (language == JarvisLanguage.BENGALI) "শিরোনাম (যেমন: ওয়াইফাই)" else "Title (e.g. WiFi Key)",
                            color = JarvisTextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("add_memory_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisTextPrimary,
                        unfocusedTextColor = JarvisTextPrimary,
                        focusedBorderColor = JarvisCyan,
                        unfocusedBorderColor = JarvisSurfaceBorder
                    )
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = {
                        Text(
                            text = if (language == JarvisLanguage.BENGALI) "গোপনীয় তথ্য বা বিস্তারিত" else "Confidential Details",
                            color = JarvisTextSecondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth().testTag("add_memory_content"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisTextPrimary,
                        unfocusedTextColor = JarvisTextPrimary,
                        focusedBorderColor = JarvisCyan,
                        unfocusedBorderColor = JarvisSurfaceBorder
                    ),
                    maxLines = 4
                )

                // Category chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    categories.forEach { cat ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (category == cat) JarvisCyan else JarvisSurfaceVariant)
                                .clickable { category = cat }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = cat,
                                color = if (category == cat) JarvisBackground else JarvisTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onSave(title, content, category)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                modifier = Modifier.testTag("save_memory_confirm_button")
            ) {
                Text(
                    text = if (language == JarvisLanguage.BENGALI) "এনক্রিপ্ট করে সেভ" else "Encrypt & Save",
                    color = JarvisBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (language == JarvisLanguage.BENGALI) "বাতিল" else "Cancel",
                    color = JarvisTextSecondary
                )
            }
        }
    )
}
