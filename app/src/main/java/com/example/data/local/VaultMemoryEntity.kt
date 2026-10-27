package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_memories")
data class VaultMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val encryptedData: String,
    val iv: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false
)
