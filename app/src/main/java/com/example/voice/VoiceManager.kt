package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.JarvisLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val tag = "VoiceManager"

    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private var speechRecognizer: SpeechRecognizer? = null

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _audioAmplitude = MutableStateFlow(0f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    private val _recognizedText = MutableStateFlow<String?>(null)
    val recognizedText: StateFlow<String?> = _recognizedText.asStateFlow()

    private var onSpeechResultCallback: ((String) -> Unit)? = null

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.e(tag, "Failed to instantiate TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            setupTtsLanguage(JarvisLanguage.BENGALI)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _audioAmplitude.value = 0f
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _audioAmplitude.value = 0f
                }
            })
        } else {
            Log.e(tag, "TTS init failed with status: $status")
        }
    }

    fun setupTtsLanguage(language: JarvisLanguage) {
        if (!isTtsInitialized || tts == null) return
        val targetLocale = if (language == JarvisLanguage.BENGALI) {
            Locale("bn", "BD")
        } else {
            Locale.US
        }

        val result = tts?.setLanguage(targetLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            // Fallback to English if Bengali engine language pack isn't installed
            tts?.setLanguage(Locale.US)
        }
        tts?.setPitch(1.05f)
        tts?.setSpeechRate(1.0f)
    }

    fun speak(text: String, language: JarvisLanguage = JarvisLanguage.BENGALI) {
        if (!isTtsInitialized || tts == null) return
        setupTtsLanguage(language)
        _isSpeaking.value = true
        // Strip markdown code ticks for TTS
        val cleanText = text.replace("```[a-zA-Z]*".toRegex(), "")
            .replace("```", "")
            .replace("*", "")
            .take(600) // Speak concise chunk
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "JARVIS_UTTERANCE_${System.currentTimeMillis()}")
    }

    fun stopSpeaking() {
        tts?.stop()
        _isSpeaking.value = false
        _audioAmplitude.value = 0f
    }

    fun startListening(language: JarvisLanguage, onResult: (String) -> Unit) {
        onSpeechResultCallback = onResult
        stopSpeaking()

        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w(tag, "Speech recognition unavailable on this device")
            return
        }

        try {
            speechRecognizer?.destroy()
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                    }

                    override fun onBeginningOfSpeech() {
                        _audioAmplitude.value = 0.5f
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize -2dB..10dB to 0..1 range
                        val norm = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1.0f)
                        _audioAmplitude.value = norm
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        _audioAmplitude.value = 0f
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _audioAmplitude.value = 0f
                        Log.d(tag, "SpeechRecognizer error: $error")
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _audioAmplitude.value = 0f
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            _recognizedText.value = text
                            onSpeechResultCallback?.invoke(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()
                        if (!text.isNullOrBlank()) {
                            _recognizedText.value = text
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                val langTag = if (language == JarvisLanguage.BENGALI) "bn-BD" else "en-US"
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e(tag, "Error starting SpeechRecognizer", e)
            _isListening.value = false
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e(tag, "Error stopping SpeechRecognizer", e)
        }
        _isListening.value = false
        _audioAmplitude.value = 0f
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
