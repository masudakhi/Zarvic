package com.example.model

data class VaultMemory(
    val id: Long = 0,
    val title: String,
    val decryptedContent: String,
    val category: String = "PERSONAL", // PERSONAL, CODING, SECRET, SYSTEM
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)
