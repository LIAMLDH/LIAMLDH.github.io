package com.fenbi.collector.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class QuestionExporter(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    suspend fun exportToJson(questions: List<Question>): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val fileName = "fenbi_questions_${dateFormat.format(Date())}.json"
            val file = File(context.cacheDir, fileName)

            val exportData = ExportData(
                exportTime = System.currentTimeMillis(),
                totalCount = questions.size,
                questions = questions.map { it.toExportQuestion() }
            )

            file.writeText(gson.toJson(exportData))

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportToText(questions: List<Question>): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val fileName = "fenbi_questions_${dateFormat.format(Date())}.txt"
            val file = File(context.cacheDir, fileName)

            val content = buildString {
                appendLine("=" .repeat(50))
                appendLine("粉笔题库导出")
                appendLine("导出时间: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
                appendLine("题目数量: ${questions.size}")
                appendLine("=" .repeat(50))
                appendLine()

                questions.forEachIndexed { index, question ->
                    appendLine("【第${index + 1}题】")
                    appendLine(question.questionText)
                    appendLine()
                    appendLine("选项:")
                    val options = question.options.split("|")
                    options.forEach { appendLine("  $it") }
                    appendLine()

                    if (!question.userAnswer.isNullOrBlank()) {
                        appendLine("你的答案: ${question.userAnswer}")
                    }
                    if (!question.correctAnswer.isNullOrBlank()) {
                        appendLine("正确答案: ${question.correctAnswer}")
                    }
                    if (!question.explanation.isNullOrBlank()) {
                        appendLine("解析: ${question.explanation}")
                    }
                    appendLine()
                    appendLine("-".repeat(50))
                    appendLine()
                }
            }

            file.writeText(content)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    data class ExportData(
        val exportTime: Long,
        val totalCount: Int,
        val questions: List<ExportQuestion>
    )

    data class ExportQuestion(
        val questionText: String,
        val options: List<String>,
        val userAnswer: String?,
        val correctAnswer: String?,
        val explanation: String?,
        val questionType: String?,
        val capturedAt: Long
    )

    private fun Question.toExportQuestion() = ExportQuestion(
        questionText = questionText,
        options = options.split("|"),
        userAnswer = userAnswer,
        correctAnswer = correctAnswer,
        explanation = explanation,
        questionType = questionType,
        capturedAt = capturedAt
    )
}
