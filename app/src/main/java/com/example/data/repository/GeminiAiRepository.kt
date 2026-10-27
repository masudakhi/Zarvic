package com.example.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.example.model.JarvisLanguage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.GenerateContentResponse
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.TextPart
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout

class GeminiAiRepository {

    private val tag = "GeminiAiRepo"

    private val generativeModel by lazy {
        try {
            val systemContent = Content(
                role = "system",
                parts = listOf(
                    TextPart(
                        "You are JARVIS, an advanced, futuristic personal AI assistant. " +
                                "You are capable, concise, intelligent, and helpful. " +
                                "Respond primarily in the language chosen by the user (Bengali by default, or English if asked). " +
                                "When explaining code, provide clean syntax blocks. " +
                                "Always maintain an intelligent, polite, and confident tone inspired by J.A.R.V.I.S."
                    )
                )
            )

            Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = null,
                safetySettings = null,
                tools = null,
                toolConfig = null,
                systemInstruction = systemContent
            )
        } catch (e: Exception) {
            Log.e(tag, "Failed to initialize Firebase AI model", e)
            null
        }
    }

    suspend fun generateStream(
        prompt: String,
        language: JarvisLanguage
    ): Flow<String> = flow {
        val model = generativeModel
        if (model != null) {
            try {
                withTimeout(60_000L) {
                    val langInstruction = if (language == JarvisLanguage.BENGALI) {
                        "দয়া করে উত্তরটি স্বাভাবিক এবং স্পষ্ট বাংলায় দিন। প্রশ্ন: $prompt"
                    } else {
                        "Please answer concisely in English. Query: $prompt"
                    }
                    model.generateContentStream(langInstruction).collect { chunk: GenerateContentResponse ->
                        val text = chunk.text
                        if (!text.isNullOrEmpty()) {
                            emit(text)
                        }
                    }
                }
                return@flow
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(tag, "Streaming via Firebase AI failed, switching to local assistant logic: ${e.message}")
            }
        }

        // Offline / Fallback local intelligence
        val fallback = getLocalFallbackResponse(prompt, language)
        for (token in fallback.split(" ")) {
            emit("$token ")
            kotlinx.coroutines.delay(40)
        }
    }.flowOn(Dispatchers.IO)

    suspend fun analyzeImageWithPrompt(
        bitmap: Bitmap,
        userPrompt: String,
        language: JarvisLanguage
    ): String {
        val model = generativeModel
        if (model != null) {
            try {
                val promptText = if (language == JarvisLanguage.BENGALI) {
                    "এই ছবিটি বিস্তারিতভাবে বিশ্লেষণ করুন এবং বাংলায় উত্তর দিন: $userPrompt"
                } else {
                    "Analyze this image in detail and answer in English: $userPrompt"
                }

                val inputContent = Content(
                    role = "user",
                    parts = listOf(
                        ImagePart(bitmap),
                        TextPart(promptText)
                    )
                )

                val response: GenerateContentResponse = withTimeout(60_000L) {
                    model.generateContent(inputContent)
                }
                val text = response.text
                if (!text.isNullOrBlank()) {
                    return text
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(tag, "Multimodal vision analysis failed", e)
                return if (language == JarvisLanguage.BENGALI) {
                    "ছবি বিশ্লেষণ সম্পন্ন করতে সমস্যা হয়েছে: ${e.localizedMessage ?: "নেটওয়ার্ক বা এআই সংযোগ ত্রুটি"}"
                } else {
                    "Image analysis error: ${e.localizedMessage ?: "Network or AI connection error"}"
                }
            }
        }

        return if (language == JarvisLanguage.BENGALI) {
            "অফলাইন ভিশন মোড: ছবিটি সফলভাবে ক্যাপচার হয়েছে। ক্লাউড মডেল সক্রিয় হলে বিস্তারিত এআই বিবরণ প্রদর্শিত হবে।"
        } else {
            "Offline Vision Mode: Image captured successfully. Detailed AI breakdown requires active connection."
        }
    }

    private fun getLocalFallbackResponse(query: String, language: JarvisLanguage): String {
        val lower = query.lowercase().trim()
        val isBn = language == JarvisLanguage.BENGALI

        return when {
            lower.contains("time") || lower.contains("সময়") || lower.contains("কয়টা বাজে") -> {
                val now = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault()).format(java.util.Date())
                if (isBn) "বর্তমান সময়: $now । আপনার আর কোনো সহায়তা প্রয়োজন?" else "Current time is $now, Sir. How else may I assist you?"
            }
            lower.contains("date") || lower.contains("তারিখ") || lower.contains("আজকে কি বার") -> {
                val date = java.text.SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale.getDefault()).format(java.util.Date())
                if (isBn) "আজকের তারিখ: $date ।" else "Today's date is $date."
            }
            lower.contains("who are you") || lower.contains("তুমি কে") || lower.contains("তোমার পরিচয়") -> {
                if (isBn) {
                    "আমি জার্ভিস (J.A.R.V.I.S.) — আপনার আধুনিক পার্সোনাল এআই অ্যাসিস্ট্যান্ট। আমি ভয়েস কমান্ড, ক্যামেরা ভিশন, মেমরি ভল্ট ও কোডিং সহায়তায় সর্বদা নিয়োজিত।"
                } else {
                    "I am J.A.R.V.I.S. — your advanced personal AI assistant, ready to assist with voice commands, vision analysis, encrypted memory, and smart automation."
                }
            }
            lower.contains("code") || lower.contains("python") || lower.contains("kotlin") || lower.contains("কোড") -> {
                if (isBn) {
                    "নিশ্চয়ই, কোডিং সহায়তার জন্য কোড স্নিপেট তৈরি করছি:\n```kotlin\nfun jarvisStatus() {\n    println(\"JARVIS Core Online: 100%\")\n}\n```\nঅন্য কোনো প্রোগ্রামিং ভাষা বা লজিক প্রয়োজন?"
                } else {
                    "Certainly. Here is a starter Kotlin implementation:\n```kotlin\nfun jarvisStatus() {\n    println(\"JARVIS Core Online: 100%\")\n}\n```\nWhat specific algorithms or framework would you like to build?"
                }
            }
            else -> {
                if (isBn) {
                    "জার্ভিস সিস্টেম সক্রিয়। আপনার অনুরোধ বিশ্লেষণ করা হয়েছে: \"$query\"। আমি আপনার সহায়তায় প্রস্তুত।"
                } else {
                    "JARVIS protocols engaged. Processed your query: \"$query\". All systems nominal and ready for instructions."
                }
            }
        }
    }
}
