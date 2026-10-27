package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JarvisLanguage
import com.example.model.JarvisMessage
import com.example.model.MessageSender
import com.example.ui.components.ArcReactorVoiceVisualizer
import com.example.ui.components.CodeBlockView
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisBlue
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisNeonGreen
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextCyan
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun ChatScreen(
    messages: List<JarvisMessage>,
    language: JarvisLanguage,
    isListening: Boolean,
    isSpeaking: Boolean,
    amplitude: Float,
    isLoading: Boolean,
    onSendMessage: (String) -> Unit,
    onToggleVoiceInput: () -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = if (language == JarvisLanguage.BENGALI) {
        listOf(
            "জার্ভিস, তোমার পরিচয় দাও",
            "বর্তমান সময় ও সিস্টেম স্ট্যাটাস",
            "একটি Kotlin কোড স্নিপেট লেখো",
            "আজকের আবহাওয়া ও আপডেট",
            "মেমরি ভল্ট কিভাবে কাজ করে?"
        )
    } else {
        listOf(
            "JARVIS, introduce yourself",
            "System status & telemetry",
            "Write a Kotlin function",
            "What can you do?",
            "Explain encrypted Memory Vault"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisBackground)
    ) {
        // Top Mini Arc Visualizer & Clear Chat
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ArcReactorVoiceVisualizer(
                    isListening = isListening,
                    isSpeaking = isSpeaking,
                    amplitude = amplitude,
                    size = 48.dp,
                    onClick = onToggleVoiceInput
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "জার্ভিস কোর AI" else "JARVIS Core AI",
                        color = JarvisCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (isListening) {
                            if (language == JarvisLanguage.BENGALI) "ভয়েস শুনছি…" else "Listening…"
                        } else if (isSpeaking) {
                            if (language == JarvisLanguage.BENGALI) "উত্তর দিচ্ছি…" else "Speaking…"
                        } else {
                            if (language == JarvisLanguage.BENGALI) "হাইব্রিড জেমিনি • প্রস্তুত" else "Hybrid Gemini • Ready"
                        },
                        color = if (isListening) JarvisNeonGreen else JarvisTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onClearChat,
                modifier = Modifier.testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Clear Chat",
                    tint = JarvisTextSecondary
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                MessageBubble(message = message, language = language)
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = JarvisCyan,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == JarvisLanguage.BENGALI) "জার্ভিস চিন্তা করছে…" else "JARVIS is thinking…",
                            color = JarvisTextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(JarvisSurfaceVariant)
                        .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(16.dp))
                        .clickable { onSendMessage(prompt) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = prompt,
                        color = JarvisTextCyan,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JarvisSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Voice Input Toggle Button
            IconButton(
                onClick = onToggleVoiceInput,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isListening) JarvisNeonGreen.copy(alpha = 0.2f) else JarvisSurfaceVariant)
                    .border(
                        1.dp,
                        if (isListening) JarvisNeonGreen else JarvisSurfaceBorder,
                        CircleShape
                    )
                    .testTag("voice_input_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = if (isListening) JarvisNeonGreen else JarvisCyan
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "জার্ভিসকে যেকোনো প্রশ্ন করুন…" else "Ask JARVIS anything…",
                        color = JarvisTextSecondary,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = JarvisTextPrimary,
                    unfocusedTextColor = JarvisTextPrimary,
                    focusedBorderColor = JarvisCyan,
                    unfocusedBorderColor = JarvisSurfaceBorder,
                    focusedContainerColor = JarvisBackground,
                    unfocusedContainerColor = JarvisBackground
                ),
                shape = RoundedCornerShape(20.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    }
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(JarvisCyan)
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = JarvisBackground
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: JarvisMessage, language: JarvisLanguage) {
    val isUser = message.sender == MessageSender.USER
    val isSystem = message.sender == MessageSender.SYSTEM

    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgBrush = if (isUser) {
        JarvisBlue.copy(alpha = 0.35f)
    } else if (isSystem) {
        JarvisSurfaceVariant
    } else {
        JarvisSurface
    }
    val borderCol = if (isUser) JarvisBlue else JarvisCyan.copy(alpha = 0.5f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Text(
                text = if (isUser) {
                    if (language == JarvisLanguage.BENGALI) "আপনি" else "You"
                } else if (isSystem) {
                    "SYSTEM"
                } else {
                    "J.A.R.V.I.S."
                },
                color = if (isUser) JarvisTextSecondary else JarvisCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .background(bgBrush)
                .border(
                    1.dp,
                    borderCol,
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .padding(12.dp)
        ) {
            // Check for code blocks in text
            if (message.text.contains("```")) {
                val parts = message.text.split("```")
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    parts.forEachIndexed { index, part ->
                        if (index % 2 == 1) {
                            // Code segment
                            val lines = part.trim().lines()
                            val lang = if (lines.firstOrNull()?.length ?: 0 < 15) lines.firstOrNull() else "code"
                            val codeContent = if (lines.size > 1) lines.drop(1).joinToString("\n") else part
                            CodeBlockView(code = codeContent, language = lang)
                        } else if (part.isNotBlank()) {
                            Text(
                                text = part.trim(),
                                color = JarvisTextPrimary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = message.text,
                    color = JarvisTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
