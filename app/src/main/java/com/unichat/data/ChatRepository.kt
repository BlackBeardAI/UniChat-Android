package com.unichat.data

import com.unichat.data.local.MessageDao
import com.unichat.data.local.MessageEntity
import com.unichat.data.remote.LlmApiService
import com.unichat.data.remote.Message
import com.unichat.data.remote.OpenAIRequest
import com.unichat.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ChatRepository(
    private val messageDao: MessageDao,
    private val settingsRepository: SettingsRepository
) {
    val allMessages: Flow<List<MessageEntity>> = messageDao.getAllMessages()

    private suspend fun getApiService(): LlmApiService {
        val url = settingsRepository.serverUrl.first()
        return RetrofitClient.create(url)
    }

    suspend fun sendMessage(userContent: String): Result<String> {
        return try {
            // Sauvegarde du message utilisateur
            messageDao.insertMessage(
                MessageEntity(content = userContent, isUser = true)
            )

            // Recupere l'historique recent (limité pour ne pas surcharger)
            val history = messageDao.getAllMessages()
                .first()
                .takeLast(20)
                .map {
                    Message(
                        role = if (it.isUser) "user" else "assistant",
                        content = it.content
                    )
                }

            val messages = history + Message(role = "user", content = userContent)
            val request = OpenAIRequest(messages = messages)

            val response = getApiService().chatCompletion(request)
            val botContent = response.choices?.firstOrNull()?.message?.content
                ?: "Réponse vide du serveur"

            messageDao.insertMessage(
                MessageEntity(
                    content = botContent,
                    isUser = false,
                    modelName = response.model ?: "local"
                )
            )

            Result.success(botContent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAvailableModels(): Result<List<String>> {
        return try {
            val response = getApiService().getModels()
            val models = response.data?.map { it.id } ?: emptyList()
            Result.success(models)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearHistory() {
        messageDao.clearAll()
    }
}
