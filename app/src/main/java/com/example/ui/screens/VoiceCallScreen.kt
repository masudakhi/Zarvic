package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JarvisLanguage
import com.example.ui.components.ArcReactorVoiceVisualizer
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisNeonGreen
import com.example.ui.theme.JarvisRed
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import kotlinx.coroutines.delay

@Composable
fun VoiceCallScreen(
    isListening: Boolean,
    isSpeaking: Boolean,
    amplitude: Float,
    language: JarvisLanguage,
    latestUserText: String?,
    latestAiText: String?,
    onToggleMic: () -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    var callSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            callSeconds++
        }
    }

    val minutes = callSeconds / 60
    val seconds = callSeconds % 60
    val durationString = String.format("%02d:%02d", minutes, seconds)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisBackground)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Call Status Bar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(JarvisSurfaceVariant)
                    .border(1.dp, JarvisCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isListening || isSpeaking) JarvisNeonGreen else JarvisCyan)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "লাইভ ভয়েস লিঙ্ক সক্রিয়" else "LIVE VOICE LINK ACTIVE",
                        color = JarvisCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = durationString,
                color = JarvisTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = if (isSpeaking) {
                    if (language == JarvisLanguage.BENGALI) "জার্ভিস কথা বলছে…" else "JARVIS is speaking…"
                } else if (isListening) {
                    if (language == JarvisLanguage.BENGALI) "আপনার কণ্ঠ শুনছি…" else "Listening to your voice…"
                } else {
                    if (language == JarvisLanguage.BENGALI) "কথা বলুন, জার্ভিস প্রস্তুত" else "Speak freely, JARVIS is ready"
                },
                color = if (isSpeaking) JarvisCyan else if (isListening) JarvisNeonGreen else JarvisTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Center: Futuristic Animated Holographic Arc Reactor Core (User's Avatar)
        Box(
            modifier = Modifier.padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            ArcReactorVoiceVisualizer(
                isListening = isListening,
                isSpeaking = isSpeaking,
                amplitude = amplitude,
                size = 260.dp,
                onClick = onToggleMic
            )
        }

        // Live Subtitle & Transcript Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(JarvisSurface)
                .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(14.dp))
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isSpeaking) {
                        latestAiText ?: if (language == JarvisLanguage.BENGALI) "জার্ভিস উত্তর তৈরি করছে…" else "Formulating response…"
                    } else if (latestUserText != null) {
                        "\"$latestUserText\""
                    } else {
                        if (language == JarvisLanguage.BENGALI) "কথা বলতে মাইক্রোফোন বাটনে চাপুন বা সরাসরি কথা বলুন" else "Speak into the microphone to converse with JARVIS"
                    },
                    color = JarvisTextPrimary,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    maxLines = 4
                )
            }
        }

        // Bottom Controls (End Call, Mic Toggle, Speaker)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mute / Listen Toggle
            IconButton(
                onClick = onToggleMic,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (isListening) JarvisNeonGreen.copy(alpha = 0.2f) else JarvisSurfaceVariant)
                    .border(
                        1.dp,
                        if (isListening) JarvisNeonGreen else JarvisSurfaceBorder,
                        CircleShape
                    )
                    .testTag("call_mic_toggle")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                    contentDescription = "Toggle Mic",
                    tint = if (isListening) JarvisNeonGreen else JarvisTextSecondary,
                    modifier = Modifier.size(26.dp)
                )
            }

            // End Call Button (Big Red Button)
            IconButton(
                onClick = onEndCall,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(JarvisRed)
                    .testTag("end_call_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Call",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Audio Mode / Speaker indicator
            IconButton(
                onClick = {},
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(JarvisSurfaceVariant)
                    .border(1.dp, JarvisSurfaceBorder, CircleShape)
                    .testTag("speaker_toggle")
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speaker",
                    tint = JarvisCyan,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}
