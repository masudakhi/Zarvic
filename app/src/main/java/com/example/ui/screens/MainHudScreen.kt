package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.JarvisLanguage
import com.example.model.JarvisMode
import com.example.ui.components.HudTelemetryHeader
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisNeonGreen
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.viewmodel.JarvisViewModel

@Composable
fun MainHudScreen(
    viewModel: JarvisViewModel,
    onGoogleSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMode by viewModel.currentMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val vaultMemories by viewModel.vaultMemories.collectAsState()
    val telemetry by viewModel.telemetryInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isListening by viewModel.voiceManager.isListening.collectAsState()
    val isSpeaking by viewModel.voiceManager.isSpeaking.collectAsState()
    val amplitude by viewModel.voiceManager.audioAmplitude.collectAsState()
    val visionResult by viewModel.visionResult.collectAsState()
    val codingResult by viewModel.codingResult.collectAsState()
    val userDisplayName by viewModel.userDisplayName.collectAsState()
    val latestUserSpeech by viewModel.latestUserSpeech.collectAsState()
    val latestAiSpeech by viewModel.latestAiSpeech.collectAsState()

    var showProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisBackground),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            HudTelemetryHeader(
                telemetry = telemetry,
                language = language,
                onToggleLanguage = { viewModel.toggleLanguage() },
                userDisplayName = userDisplayName,
                onProfileClick = { showProfileDialog = true }
            )
        },
        bottomBar = {
            if (currentMode != JarvisMode.VOICE_CALL) {
                HudModeNavigationBar(
                    currentMode = currentMode,
                    language = language,
                    onSelectMode = { viewModel.setMode(it) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentMode, label = "ModeTransition") { mode ->
                when (mode) {
                    JarvisMode.ASSISTANT -> {
                        ChatScreen(
                            messages = messages,
                            language = language,
                            isListening = isListening,
                            isSpeaking = isSpeaking,
                            amplitude = amplitude,
                            isLoading = isLoading,
                            onSendMessage = { viewModel.sendMessage(it) },
                            onToggleVoiceInput = { viewModel.toggleVoiceInput() },
                            onClearChat = { viewModel.clearChat() }
                        )
                    }

                    JarvisMode.VOICE_CALL -> {
                        VoiceCallScreen(
                            isListening = isListening,
                            isSpeaking = isSpeaking,
                            amplitude = amplitude,
                            language = language,
                            latestUserText = latestUserSpeech,
                            latestAiText = latestAiSpeech,
                            onToggleMic = { viewModel.toggleVoiceInput() },
                            onEndCall = { viewModel.endVoiceCalling() }
                        )
                    }

                    JarvisMode.VISION -> {
                        VisionScreen(
                            language = language,
                            isAnalyzing = isLoading,
                            analysisResult = visionResult,
                            onAnalyzeImage = { bmp, prompt ->
                                viewModel.analyzeVision(bmp, prompt)
                            }
                        )
                    }

                    JarvisMode.MEMORY_VAULT -> {
                        MemoryVaultScreen(
                            memories = vaultMemories,
                            language = language,
                            onSaveMemory = { title, content, cat ->
                                viewModel.saveVaultMemory(title, content, cat)
                            },
                            onTogglePin = { viewModel.togglePin(it) },
                            onDeleteMemory = { viewModel.deleteVaultMemory(it) }
                        )
                    }

                    JarvisMode.CODING -> {
                        CodingScreen(
                            language = language,
                            isLoading = isLoading,
                            codeOutput = codingResult,
                            onGenerateCode = { viewModel.generateCode(it) }
                        )
                    }

                    JarvisMode.SCREEN_TOUCH -> {
                        ScreenTouchScreen(language = language)
                    }

                    JarvisMode.TELEMETRY -> {
                        TelemetryDashboardScreen(
                            telemetry = telemetry,
                            language = language,
                            onRefreshTelemetry = { viewModel.refreshTelemetry() }
                        )
                    }
                }
            }
        }
    }

    if (showProfileDialog) {
        ProfileDialog(
            language = language,
            userDisplayName = userDisplayName,
            onDismiss = { showProfileDialog = false },
            onGoogleSignIn = {
                showProfileDialog = false
                onGoogleSignInClick()
            }
        )
    }
}

@Composable
private fun HudModeNavigationBar(
    currentMode: JarvisMode,
    language: JarvisLanguage,
    onSelectMode: (JarvisMode) -> Unit
) {
    val modes = listOf(
        Pair(JarvisMode.ASSISTANT, Icons.Default.Chat),
        Pair(JarvisMode.VOICE_CALL, Icons.Default.Call),
        Pair(JarvisMode.VISION, Icons.Default.CameraAlt),
        Pair(JarvisMode.MEMORY_VAULT, Icons.Default.Lock),
        Pair(JarvisMode.CODING, Icons.Default.Code),
        Pair(JarvisMode.SCREEN_TOUCH, Icons.Default.TouchApp),
        Pair(JarvisMode.TELEMETRY, Icons.Default.Speed)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(JarvisSurface)
            .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(modes) { (mode, icon) ->
                val isSelected = currentMode == mode
                val title = if (language == JarvisLanguage.BENGALI) mode.titleBn else mode.titleEn

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) JarvisCyan.copy(alpha = 0.15f) else JarvisSurface)
                        .clickable { onSelectMode(mode) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("nav_${mode.name.lowercase()}")
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) JarvisCyan else JarvisTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        color = if (isSelected) JarvisCyan else JarvisTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileDialog(
    language: JarvisLanguage,
    userDisplayName: String?,
    onDismiss: () -> Unit,
    onGoogleSignIn: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = JarvisSurface,
        title = {
            Text(
                text = if (language == JarvisLanguage.BENGALI) "ব্যবহারকারী প্রোফাইল" else "User Profile & Identity",
                color = JarvisCyan,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // User Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, JarvisCyan, CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.user_avatar),
                        contentDescription = "User Avatar",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = userDisplayName ?: "J.A.R.V.I.S. User",
                    color = JarvisTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (language == JarvisLanguage.BENGALI) {
                        "Google ক্লাউড সিঙ্ক এবং লোকাল এনক্রিপ্ট করা ডেটা সক্রিয়"
                    } else {
                        "Google Cloud Authentication & AES-256 Local Encrypted Storage"
                    },
                    color = JarvisTextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onGoogleSignIn,
                    modifier = Modifier.fillMaxWidth().testTag("google_signin_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "Google দিয়ে সাইন-ইন করুন" else "Sign In with Google",
                        color = JarvisBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (language == JarvisLanguage.BENGALI) "ঠিক আছে" else "Close",
                    color = JarvisCyan
                )
            }
        }
    )
}
