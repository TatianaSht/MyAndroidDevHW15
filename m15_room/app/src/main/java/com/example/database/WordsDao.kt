package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WordsDao {

    @Query("SELECT * FROM words LIMIT 5")
    fun getAll(): Flow<List<Word>>

    @Insert
    suspend fun addWord(word: Word)

    @Query("SELECT * FROM words WHERE word = :word LIMIT 1")
    suspend fun getWord(word: String): Word

    @Query("UPDATE words SET count = :count WHERE word = :word")
    suspend fun updateCount(word: String, count: Int)

    @Query("DELETE FROM words")
    suspend fun delete()
}