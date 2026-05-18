package com.fenbi.collector.ui

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fenbi.collector.FenbiCollectorApp
import com.fenbi.collector.data.Question
import com.fenbi.collector.data.QuestionExporter
import com.fenbi.collector.service.CaptureForegroundService
import com.fenbi.collector.service.QuestionCaptureService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = (application as FenbiCollectorApp).database
    private val questionDao = database.questionDao()
    private val exporter = QuestionExporter(application)

    val questions: StateFlow<List<Question>> = questionDao.getAllQuestions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val questionCount: StateFlow<Int> = questionDao.getQuestionCount()
        .stateIn(viewModelScope, SharingStarted.Lazily, 0)

    private val _isServiceRunning = MutableStateFlow(false)
    val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

    private val _isAccessibilityEnabled = MutableStateFlow(false)
    val isAccessibilityEnabled: StateFlow<Boolean> = _isAccessibilityEnabled.asStateFlow()

    private val _exportResult = MutableSharedFlow<Result<android.net.Uri>>()
    val exportResult: SharedFlow<Result<android.net.Uri>> = _exportResult.asSharedFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        checkAccessibilityStatus()
    }

    fun checkAccessibilityStatus() {
        val enabledServices = Settings.Secure.getString(
            getApplication<Application>().contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        _isAccessibilityEnabled.value = enabledServices?.contains(
            ComponentName(getApplication(), QuestionCaptureService::class.java).flattenToString()
        ) == true
    }

    fun toggleService() {
        viewModelScope.launch {
            if (_isServiceRunning.value) {
                stopService()
            } else {
                startService()
            }
        }
    }

    private fun startService() {
        if (!_isAccessibilityEnabled.value) {
            viewModelScope.launch {
                _toastMessage.emit("请先开启无障碍服务权限")
            }
            return
        }

        val context = getApplication<Application>()
        val intent = Intent(context, CaptureForegroundService::class.java).apply {
            action = CaptureForegroundService.ACTION_START
        }
        context.startForegroundService(intent)
        _isServiceRunning.value = true
    }

    private fun stopService() {
        val context = getApplication<Application>()
        val intent = Intent(context, CaptureForegroundService::class.java).apply {
            action = CaptureForegroundService.ACTION_STOP
        }
        context.startService(intent)
        _isServiceRunning.value = false
    }

    fun exportToJson() {
        viewModelScope.launch {
            val questionsList = questionDao.getAllQuestionsSync()
            if (questionsList.isEmpty()) {
                _toastMessage.emit("没有可导出的题目")
                return@launch
            }
            val result = exporter.exportToJson(questionsList)
            _exportResult.emit(result)
            result.onSuccess {
                _toastMessage.emit("导出成功")
            }.onFailure {
                _toastMessage.emit("导出失败: ${it.message}")
            }
        }
    }

    fun exportToText() {
        viewModelScope.launch {
            val questionsList = questionDao.getAllQuestionsSync()
            if (questionsList.isEmpty()) {
                _toastMessage.emit("没有可导出的题目")
                return@launch
            }
            val result = exporter.exportToText(questionsList)
            _exportResult.emit(result)
            result.onSuccess {
                _toastMessage.emit("导出成功")
            }.onFailure {
                _toastMessage.emit("导出失败: ${it.message}")
            }
        }
    }

    fun clearAllQuestions() {
        viewModelScope.launch {
            questionDao.deleteAll()
            _toastMessage.emit("已清空所有题目")
        }
    }

    fun deleteQuestion(question: Question) {
        viewModelScope.launch {
            questionDao.delete(question)
            _toastMessage.emit("已删除题目")
        }
    }

    fun search(keyword: String): Flow<List<Question>> {
        return if (keyword.isBlank()) {
            questions
        } else {
            questionDao.search(keyword)
        }
    }
}
