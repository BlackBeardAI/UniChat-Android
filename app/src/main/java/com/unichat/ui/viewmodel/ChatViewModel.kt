package com.unichat.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.unichat.data.ChatRepository
import com.unichat.data.SettingsRepository
import com.unichat.data.local.AppDatabase
import com.unichat.data.local.MessageEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<MessageEntity> = emptyList(),
    val inputText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val availableModels: List<String> = emptyList(),
    val selectedModel: String = "local",
    val serverUrl: String = "http://192.168.1.100:8080/v1/"
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val settingsRepository = SettingsRepository(application)
    private val chatRepository = ChatRepository(database.messageDao(), settingsRepository)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            chatRepository.allMessages.collect { messages ->
                _uiState.update { it.copy(messages = messages) }
            }
        }
        viewModelScope.launch {
            settingsRepository.serverUrl.collect { url ->
                _uiState.update { it.copy(serverUrl = url) }
            }
        }
        viewModelScope.launch {
            settingsRepository.selectedModel.collect { model ->
                _uiState.update { it.copy(selectedModel = model) }
            }
        }
        loadModels()
    }

    fun onInputChange(text: String) {
        _uiState.update { it.copy(inputText = text, errorMessage = null) }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isEmpty()) return

        _uiState.update { it.copy(inputText = "", isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = chatRepository.sendMessage(text)
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun loadModels() {
        viewModelScope.launch {
            val result = chatRepository.getAvailableModels()
            result.getOrNull()?.let { models ->
                _uiState.update { it.copy(availableModels = models) }
            }
        }
    }

    fun selectModel(model: String) {
        viewModelScope.launch {
            settingsRepository.saveSelectedModel(model)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            chatRepository.clearHistory()
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun updateServerUrl(url: String) {
        viewModelScope.launch {
            settingsRepository.saveServerUrl(url)
            loadModels()
        }
    }
}
