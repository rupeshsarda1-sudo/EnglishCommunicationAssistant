package com.rupesh.englishassistant.service

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rupesh.englishassistant.model.HistoryEntry

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT 100")
    fun getAllHistory(): LiveData<List<HistoryEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: HistoryEntry)

    @Delete
    suspend fun deleteEntry(entry: HistoryEntry)

    @Query("DELETE FROM history")
    suspend fun clearAll()
}
