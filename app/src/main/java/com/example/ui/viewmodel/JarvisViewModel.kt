package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.JarvisDatabase
import com.example.data.repository.GeminiAiRepository
import com.example.data.repository.MemoryVaultRepository
import com.example.data.repository.TelemetryRepository
import com.example.model.JarvisLanguage
import com.example.model.JarvisMessage
import com.example.model.JarvisMode
import com.example.model.MessageSender
import com.example.model.TelemetryInfo
import com.example.model.VaultMemory
import com.example.voice.VoiceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JarvisViewModel(application: Application) : AndroidViewModel(application) {

    private val db = JarvisDatabase.getInstance(application)
    private val vaultRepo = MemoryVaultRepository(db.vaultMemoryDao())
    private val aiRepo = GeminiAiRepository()
    private val telemetryRepo = TelemetryRepository(application)
    val voiceManager = VoiceManager(application)

    private val _currentMode = MutableStateFlow(JarvisMode.ASSISTANT)
    val currentMode: StateFlow<JarvisMode> = _currentMode.asStateFlow()

    private val _language = MutableStateFlow(JarvisLanguage.BENGALI)
    val language: StateFlow<JarvisLanguage> = _language.asStateFlow()

    private val _messages = MutableStateFlow<List<JarvisMessage>>(emptyList())
    val messages: StateFlow<List<JarvisMessage>> = _messages.asStateFlow()

    val vaultMemories: StateFlow<List<VaultMemory>> = vaultRepo.getMemoriesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _telemetryInfo = MutableStateFlow(telemetryRepo.getTelemetryInfo())
    val telemetryInfo: StateFlow<TelemetryInfo> = _telemetryInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _visionResult = MutableStateFlow<String?>(null)
    val visionResult: StateFlow<String?> = _visionResult.asStateFlow()

    private val _codingResult = MutableStateFlow<String?>(null)
    val codingResult: StateFlow<String?> = _codingResult.asStateFlow()

    private val _userDisplayName = MutableStateFlow<String?>("JARVIS Core")
    val userDisplayName: StateFlow<String?> = _userDisplayName.asStateFlow()

    private val _latestUserSpeech = MutableStateFlow<String?>(null)
    val latestUserSpeech: StateFlow<String?> = _latestUserSpeech.asStateFlow()

    private val _latestAiSpeech = MutableStateFlow<String?>(null)
    val latestAiSpeech: StateFlow<String?> = _latestAiSpeech.asStateFlow()

    init {
        // Welcome initial greeting from JARVIS
        val initialGreeting = if (_language.value == JarvisLanguage.BENGALI) {
            "অনলাইন। জার্ভিস সিস্টেম পুরোপুরি সক্রিয়। ভয়েস কল, ক্যামেরা ভিশন এবং এনক্রিপ্ট করা মেমরি ভল্ট প্রস্তুত। আপনাকে কীভাবে সহায়তা করতে পারি, স্যার?"
        } else {
            "All systems online. J.A.R.V.I.S. is ready to assist. Voice calling, vision scanning, and encrypted memory vault are active."
        }
        _messages.value = listOf(
            JarvisMessage(
                sender = MessageSender.JARVIS,
                text = initialGreeting
            )
        )
    }

    fun setMode(mode: JarvisMode) {
        _currentMode.value = mode
    }

    fun toggleLanguage() {
        val newLang = if (_language.value == JarvisLanguage.BENGALI) JarvisLanguage.ENGLISH else JarvisLanguage.BENGALI
        _language.value = newLang
        voiceManager.setupTtsLanguage(newLang)
    }

    fun sendMessage(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = JarvisMessage(sender = MessageSender.USER, text = prompt)
        _messages.value = _messages.value + userMsg
        _isLoading.value = true

        viewModelScope.launch {
            val jarvisMsgId = java.util.UUID.randomUUID().toString()
            val initialJarvisMsg = JarvisMessage(
                id = jarvisMsgId,
                sender = MessageSender.JARVIS,
                text = "",
                isStreaming = true
            )
            _messages.value = _messages.value + initialJarvisMsg

            val responseBuilder = StringBuilder()
            try {
                aiRepo.generateStream(prompt, _language.value).collect { chunk ->
                    responseBuilder.append(chunk)
                    val updated = responseBuilder.toString()
                    _messages.value = _messages.value.map { msg ->
                        if (msg.id == jarvisMsgId) msg.copy(text = updated) else msg
                    }
                }
                val finalText = responseBuilder.toString()
                _messages.value = _messages.value.map { msg ->
                    if (msg.id == jarvisMsgId) msg.copy(text = finalText, isStreaming = false) else msg
                }
                _latestAiSpeech.value = finalText

                // Speak if in voice mode or assistant
                if (_currentMode.value == JarvisMode.VOICE_CALL) {
                    voiceManager.speak(finalText, _language.value)
                }
            } catch (e: Exception) {
                val errorMsg = if (_language.value == JarvisLanguage.BENGALI) {
                    "দুঃখিত, সংযোগে ত্রুটি হয়েছে: ${e.localizedMessage ?: "অজ্ঞাত সমস্যা"}"
                } else {
                    "Apologies, an error occurred: ${e.localizedMessage ?: "Unknown issue"}"
                }
                _messages.value = _messages.value.map { msg ->
                    if (msg.id == jarvisMsgId) msg.copy(text = errorMsg, isStreaming = false) else msg
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleVoiceInput() {
        if (voiceManager.isListening.value) {
            voiceManager.stopListening()
        } else {
            voiceManager.startListening(_language.value) { spokenText ->
                _latestUserSpeech.value = spokenText
                sendMessage(spokenText)
            }
        }
    }

    fun startVoiceCalling() {
        _currentMode.value = JarvisMode.VOICE_CALL
        voiceManager.startListening(_language.value) { spokenText ->
            _latestUserSpeech.value = spokenText
            sendMessage(spokenText)
        }
    }

    fun endVoiceCalling() {
        voiceManager.stopListening()
        voiceManager.stopSpeaking()
        _currentMode.value = JarvisMode.ASSISTANT
    }

    fun clearChat() {
        _messages.value = emptyList()
    }

    fun saveVaultMemory(title: String, content: String, category: String) {
        viewModelScope.launch {
            vaultRepo.saveMemory(title, content, category)
        }
    }

    fun togglePin(memory: VaultMemory) {
        viewModelScope.launch {
            vaultRepo.togglePin(memory)
        }
    }

    fun deleteVaultMemory(id: Long) {
        viewModelScope.launch {
            vaultRepo.deleteMemory(id)
        }
    }

    fun analyzeVision(bitmap: Bitmap, prompt: String) {
        _isLoading.value = true
        _visionResult.value = null
        viewModelScope.launch {
            val result = aiRepo.analyzeImageWithPrompt(bitmap, prompt, _language.value)
            _visionResult.value = result
            _isLoading.value = false
        }
    }

    fun generateCode(query: String) {
        _isLoading.value = true
        _codingResult.value = null
        viewModelScope.launch {
            val codePrompt = "Please generate clean, robust, commented code for: $query. Provide the code in Markdown code blocks and brief explanation."
            val responseBuilder = StringBuilder()
            try {
                aiRepo.generateStream(codePrompt, _language.value).collect { chunk ->
                    responseBuilder.append(chunk)
                    _codingResult.value = responseBuilder.toString()
                }
            } catch (e: Exception) {
                _codingResult.value = "// Error: ${e.localizedMessage ?: "Failed to generate"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshTelemetry() {
        _telemetryInfo.value = telemetryRepo.getTelemetryInfo()
    }

    fun setUserDisplayName(name: String?) {
        _userDisplayName.value = name
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
    }
}
