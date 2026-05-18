package com.fenbi.collector.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionText: String,
    val options: String,
    val userAnswer: String?,
    val correctAnswer: String?,
    val explanation: String?,
    val questionType: String?,
    val source: String?,
    val capturedAt: Long = System.currentTimeMillis(),
    val hash: String
)
