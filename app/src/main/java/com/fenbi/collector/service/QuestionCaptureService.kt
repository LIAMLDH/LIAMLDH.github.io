package com.fenbi.collector.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.fenbi.collector.FenbiCollectorApp
import com.fenbi.collector.data.Question
import kotlinx.coroutines.*
import java.security.MessageDigest

class QuestionCaptureService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val questionParser = QuestionParser()
    private val seenHashes = mutableSetOf<String>()

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                    AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.packageName?.toString()?.contains("fenbi") == true ||
            event?.packageName?.toString()?.contains("粉笔") == true) {

            val rootNode = rootInActiveWindow ?: return

            if (questionParser.isQuestionPage(rootNode)) {
                serviceScope.launch {
                    try {
                        val question = questionParser.parseQuestion(rootNode)
                        if (question != null && !seenHashes.contains(question.hash)) {
                            seenHashes.add(question.hash)
                            saveQuestion(question)
                            broadcastQuestionCaptured(question)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            rootNode.recycle()
        }
    }

    private suspend fun saveQuestion(question: Question) {
        val app = application as FenbiCollectorApp
        val existing = app.database.questionDao().getByHash(question.hash)
        if (existing == null) {
            app.database.questionDao().insert(question)
        }
    }

    private fun broadcastQuestionCaptured(question: Question) {
        val intent = Intent(ACTION_QUESTION_CAPTURED).apply {
            putExtra(EXTRA_QUESTION_TEXT, question.questionText)
            setPackage(packageName)
        }
        sendBroadcast(intent)
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_QUESTION_CAPTURED = "com.fenbi.collector.QUESTION_CAPTURED"
        const val EXTRA_QUESTION_TEXT = "question_text"
    }
}

class QuestionParser {

    private val questionPatterns = listOf(
        Regex("[1１]\\.[\\s\\S]+?(?=[2２]\\.|A[．.、]|答案|$)"),
        Regex("第[一二三四五六七八九十\\d]+题[\\s\\S]+?(?=第[一二三四五六七八九十\\d]+题|答案|$)")
    )

    private val optionPatterns = listOf(
        Regex("[AＡa][．.、:：][^\\n]+"),
        Regex("[1１][．.、:：][^\\n]+"),
        Regex("[aａ][．.、:：][^\\n]+")
    )

    private val correctAnswerPatterns = listOf(
        Regex("正确答案[：:]\\s*([A-Ｚa-ｚ\\d])"),
        Regex("答案[：:]\\s*([A-Ｚa-ｚ\\d])"),
        Regex("正确答案是[：:]?\\s*([A-Ｚa-ｚ\\d])"),
        Regex("答[：:]\\s*([A-Ｚa-ｚ\\d])")
    )

    private val userAnswerPatterns = listOf(
        Regex("你的答案[：:]\\s*([A-Ｚa-ｚ\\d])"),
        Regex("已选[：:]\\s*([A-Ｚa-ｚ\\d])")
    )

    private val explanationPatterns = listOf(
        Regex("【?解析[】]?[：:]?\\s*[\\s\\S]+?(?=【|\\z)"),
        Regex("答案解析[：:]?\\s*[\\s\\S]+?(?=考点|\\z)")
    )

    fun isQuestionPage(rootNode: AccessibilityNodeInfo): Boolean {
        val text = getAllText(rootNode)
        return text.contains("题") && (
            text.contains("选项") || text.contains("答案") ||
            text.contains("解析") || text.contains("A.") ||
            text.contains("A.")
        )
    }

    fun parseQuestion(rootNode: AccessibilityNodeInfo): Question? {
        val fullText = getAllText(rootNode)
        if (fullText.length < 10) return null

        val questionText = extractQuestionText(fullText) ?: return null
        val options = extractOptions(fullText)
        val correctAnswer = extractCorrectAnswer(fullText)
        val userAnswer = extractUserAnswer(fullText)
        val explanation = extractExplanation(fullText)
        val hash = generateHash(questionText, options)

        return Question(
            questionText = questionText,
            options = options.joinToString("|"),
            userAnswer = userAnswer,
            correctAnswer = correctAnswer,
            explanation = explanation,
            questionType = detectQuestionType(fullText),
            source = detectSource(fullText),
            hash = hash
        )
    }

    private fun getAllText(node: AccessibilityNodeInfo): String {
        val sb = StringBuilder()
        collectText(node, sb)
        return sb.toString()
    }

    private fun collectText(node: AccessibilityNodeInfo, sb: StringBuilder) {
        node.text?.toString()?.let { text ->
            if (text.isNotBlank()) {
                sb.append(text).append("\n")
            }
        }
        node.contentDescription?.toString()?.let { desc ->
            if (desc.isNotBlank()) {
                sb.append(desc).append("\n")
            }
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { child ->
                collectText(child, sb)
                child.recycle()
            }
        }
    }

    private fun extractQuestionText(fullText: String): String? {
        for (pattern in questionPatterns) {
            val match = pattern.find(fullText)
            if (match != null) {
                val text = match.value.trim()
                if (text.length > 5) {
                    return text.substringAfter(Regex("[1１]\\.").find(text)?.value ?: "")
                        .substringAfter(Regex("[2２]\\.").find(text)?.value ?: "")
                        .trim()
                        .ifEmpty { text }
                }
            }
        }

        val lines = fullText.lines().filter { it.isNotBlank() }
        for (i in lines.indices) {
            val line = lines[i]
            if ((line.contains("题") || line.contains("?")) && line.length > 10) {
                val questionLines = mutableListOf(line)
                for (j in i + 1 until lines.size) {
                    val nextLine = lines[j]
                    if (optionPatterns.any { it.containsMatchIn(nextLine) } ||
                        nextLine.contains("答案") ||
                        nextLine.contains("解析")) {
                        break
                    }
                    questionLines.add(nextLine)
                }
                return questionLines.joinToString(" ").trim()
            }
        }
        return null
    }

    private fun extractOptions(fullText: String): List<String> {
        val options = mutableListOf<String>()

        for (pattern in optionPatterns) {
            pattern.findAll(fullText).forEach { match ->
                val option = match.value.trim()
                if (option.length > 2 && option.length < 500) {
                    options.add(option)
                }
            }
        }

        if (options.size >= 2) {
            return options.distinct().take(6)
        }

        val lines = fullText.lines()
        for (i in lines.indices) {
            val line = lines[i]
            if (line.matches(Regex("[A-Ｚa-ｚ][．.、:：\\s].+")) ||
                line.matches(Regex("[1１][．.、:：\\s].+"))) {
                options.add(line.trim())
            }
        }

        return options.distinct().take(6)
    }

    private fun extractCorrectAnswer(fullText: String): String? {
        for (pattern in correctAnswerPatterns) {
            val match = pattern.find(fullText)
            if (match != null) {
                return match.groupValues[1].uppercase()
            }
        }
        return null
    }

    private fun extractUserAnswer(fullText: String): String? {
        for (pattern in userAnswerPatterns) {
            val match = pattern.find(fullText)
            if (match != null) {
                return match.groupValues[1].uppercase()
            }
        }
        return null
    }

    private fun extractExplanation(fullText: String): String? {
        for (pattern in explanationPatterns) {
            val match = pattern.find(fullText)
            if (match != null) {
                return match.value
                    .replace(Regex("【?解析[】]?[：:]?\\s*"), "")
                    .trim()
                    .take(2000)
            }
        }
        return null
    }

    private fun detectQuestionType(fullText: String): String {
        return when {
            fullText.contains("单选") -> "单选题"
            fullText.contains("多选") -> "多选题"
            fullText.contains("判断") -> "判断题"
            fullText.contains("填空") -> "填空题"
            else -> "未知"
        }
    }

    private fun detectSource(fullText: String): String? {
        val patterns = listOf(
            Regex("来源[：:]\\s*([^\\n]+)"),
            Regex("出处[：:]\\s*([^\\n]+)"),
            Regex("来自[：:]\\s*([^\\n]+)")
        )
        for (pattern in patterns) {
            val match = pattern.find(fullText)
            if (match != null) {
                return match.groupValues[1].trim().take(100)
            }
        }
        return "粉笔App"
    }

    private fun generateHash(questionText: String, options: List<String>): String {
        val input = "$questionText|${options.joinToString()}"
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
