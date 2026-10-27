package com.example.model

enum class MessageSender {
    USER,
    JARVIS,
    SYSTEM
}

data class JarvisMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isCode: Boolean = false,
    val codeLanguage: String? = null,
    val imageUri: String? = null,
    val isStreaming: Boolean = false
)
