package com.example.data.repository

import com.example.data.local.EncryptionHelper
import com.example.data.local.VaultMemoryDao
import com.example.data.local.VaultMemoryEntity
import com.example.model.VaultMemory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MemoryVaultRepository(private val dao: VaultMemoryDao) {

    fun getMemoriesFlow(): Flow<List<VaultMemory>> {
        return dao.getAllMemoriesFlow().map { entities ->
            entities.map { entity ->
                val decrypted = EncryptionHelper.decrypt(entity.encryptedData, entity.iv)
                VaultMemory(
                    id = entity.id,
                    title = entity.title,
                    decryptedContent = decrypted,
                    category = entity.category,
                    createdAt = entity.createdAt,
                    isPinned = entity.isPinned
                )
            }
        }
    }

    suspend fun saveMemory(title: String, plainContent: String, category: String = "PERSONAL", isPinned: Boolean = false): Long {
        val (encryptedBase64, ivBase64) = EncryptionHelper.encrypt(plainContent)
        val entity = VaultMemoryEntity(
            title = title.trim(),
            encryptedData = encryptedBase64,
            iv = ivBase64,
            category = category,
            isPinned = isPinned
        )
        return dao.insertMemory(entity)
    }

    suspend fun togglePin(memory: VaultMemory) {
        val (encryptedBase64, ivBase64) = EncryptionHelper.encrypt(memory.decryptedContent)
        val entity = VaultMemoryEntity(
            id = memory.id,
            title = memory.title,
            encryptedData = encryptedBase64,
            iv = ivBase64,
            category = memory.category,
            createdAt = memory.createdAt,
            isPinned = !memory.isPinned
        )
        dao.updateMemory(entity)
    }

    suspend fun deleteMemory(id: Long) {
        dao.deleteById(id)
    }
}
