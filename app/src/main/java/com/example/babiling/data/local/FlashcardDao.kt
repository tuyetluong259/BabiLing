package com.example.babiling.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.babiling.data.model.FlashcardEntity

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards WHERE topicId = :topicId")
    suspend fun getFlashcardsByTopic(topicId: String): List<FlashcardEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<FlashcardEntity>)

    @Query("SELECT COUNT(*) FROM flashcards")
    suspend fun countAll(): Int
}
