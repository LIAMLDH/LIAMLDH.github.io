package com.fenbi.collector.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY capturedAt DESC")
    fun getAllQuestions(): Flow<List<Question>>

    @Query("SELECT * FROM questions ORDER BY capturedAt DESC")
    suspend fun getAllQuestionsSync(): List<Question>

    @Query("SELECT COUNT(*) FROM questions")
    fun getQuestionCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCountSync(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: Question): Long

    @Delete
    suspend fun delete(question: Question)

    @Query("DELETE FROM questions")
    suspend fun deleteAll()

    @Query("SELECT * FROM questions WHERE hash = :hash LIMIT 1")
    suspend fun getByHash(hash: String): Question?

    @Query("SELECT * FROM questions WHERE questionText LIKE '%' || :keyword || '%' OR explanation LIKE '%' || :keyword || '%' ORDER BY capturedAt DESC")
    fun search(keyword: String): Flow<List<Question>>
}
