package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultMemoryDao {
    @Query("SELECT * FROM vault_memories ORDER BY isPinned DESC, createdAt DESC")
    fun getAllMemoriesFlow(): Flow<List<VaultMemoryEntity>>

    @Query("SELECT * FROM vault_memories WHERE title LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchMemoriesFlow(query: String): Flow<List<VaultMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemory(entity: VaultMemoryEntity): Long

    @Update
    suspend fun updateMemory(entity: VaultMemoryEntity)

    @Delete
    suspend fun deleteMemory(entity: VaultMemoryEntity)

    @Query("DELETE FROM vault_memories WHERE id = :id")
    suspend fun deleteById(id: Long)
}
