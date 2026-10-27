package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JarvisLanguage
import com.example.ui.components.HudCard
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisNeonGreen
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun ScreenTouchScreen(
    language: JarvisLanguage,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var simulatedTapCount by remember { mutableIntStateOf(0) }
    var simulatedScreenNodes by remember {
        mutableStateOf(
            if (language == JarvisLanguage.BENGALI) {
                "স্ক্রিন রিডিং নোড ট্রি:\n• [Header] 'JARVIS Command Hub'\n• [Button] 'ভয়েস কল শুরু' (bounds: [20, 150, 380, 210])\n• [Card] 'সিস্টেম স্ট্যাটাস: অপ্টিমাল'"
            } else {
                "Screen Reading Node Tree:\n• [Header] 'JARVIS Command Hub'\n• [Button] 'Start Voice Call' (bounds: [20, 150, 380, 210])\n• [Card] 'System Status: Optimal'"
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        HudCard(
            borderColor = JarvisCyan.copy(alpha = 0.5f),
            glowCyan = true
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(JarvisCyan.copy(alpha = 0.15f))
                        .border(1.dp, JarvisCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Touch",
                        tint = JarvisCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "অটো স্ক্রিন রিডিং ও টাচ কন্ট্রোল" else "AUTO SCREEN READING & TOUCH",
                        color = JarvisCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "অ্যাক্সেসিবিলিটি ফ্রেমওয়ার্ক ইন্টিগ্রেশন" else "Android Accessibility Service Architecture",
                        color = JarvisTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Accessibility Service Capability Card
        HudCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (language == JarvisLanguage.BENGALI) "সিস্টেম সামর্থ্য ও অনুমতি" else "SYSTEM CAPABILITIES & POLICIES",
                    color = JarvisCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Text(
                    text = if (language == JarvisLanguage.BENGALI) {
                        "Android OS নীতিমালায় স্ক্রিনের তথ্য পড়া এবং স্বয়ংক্রিয় স্পর্শের জন্য AccessibilityService API ব্যবহৃত হয়। এই সিস্টেমে কোনো অনিরাপদ বা অনুমোদনহীন কোড চালানো হয় না।"
                    } else {
                        "Android OS AccessibilityService framework governs screen reading (AccessibilityNodeInfo tree) and automated gesture dispatching (dispatchGesture) under full user privacy controls."
                    },
                    color = JarvisTextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )

                Button(
                    onClick = {
                        try {
                            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("open_accessibility_settings"),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceVariant),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Accessibility,
                        contentDescription = "Accessibility",
                        tint = JarvisCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "Android অ্যাক্সেসিবিলিটি সেটিংস খুলুন" else "Open Accessibility Settings",
                        color = JarvisTextPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Interactive Simulation Test Sandbox
        HudCard(borderColor = JarvisCyan.copy(alpha = 0.4f)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "সিমুলেশন ও ইন্টারেক্টিভ টেস্ট" else "SIMULATION & TEST SANDBOX",
                        color = JarvisCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(JarvisNeonGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "READY",
                            color = JarvisNeonGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Interactive touch target
                Button(
                    onClick = {
                        simulatedTapCount++
                        simulatedScreenNodes = if (language == JarvisLanguage.BENGALI) {
                            "স্বয়ংক্রিয় টাচ রেজিস্টার্ড:\n• ইভেন্ট: ACTION_DOWN -> ACTION_UP\n• স্থানাঙ্ক: x=540, y=1200\n• টার্গেট সফলভাবে $simulatedTapCount বার ক্লিক হয়েছে।"
                        } else {
                            "Automated Touch Dispatched:\n• Event: ACTION_DOWN -> ACTION_UP\n• Target coordinates: (x=540, y=1200)\n• Click count: $simulatedTapCount"
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("simulated_touch_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Tap",
                        tint = JarvisBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "স্বয়ংক্রিয় স্পর্শ টেস্ট করুন ($simulatedTapCount)" else "Dispatch Simulated Touch ($simulatedTapCount)",
                        color = JarvisBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Node Inspector Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(JarvisBackground)
                        .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = simulatedScreenNodes,
                        color = JarvisTextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
