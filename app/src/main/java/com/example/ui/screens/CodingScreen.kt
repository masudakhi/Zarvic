package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JarvisLanguage
import com.example.ui.components.CodeBlockView
import com.example.ui.components.HudCard
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextCyan
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun CodingScreen(
    language: JarvisLanguage,
    isLoading: Boolean,
    codeOutput: String?,
    onGenerateCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var codeQuery by remember { mutableStateOf("") }

    val presetTemplates = if (language == JarvisLanguage.BENGALI) {
        listOf(
            "Kotlin Coroutines ও Flow উদাহরণ",
            "Python ডেটা স্ট্রাকচার ও অ্যালগরিদম",
            "Android Jetpack Compose UI উপাদান",
            "REST API ফেচ করার কোড"
        )
    } else {
        listOf(
            "Kotlin Coroutines & Flow demo",
            "Python data structures snippet",
            "Android Compose modern UI card",
            "Secure AES encryption in Kotlin"
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
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Coding",
                        tint = JarvisCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "কোডিং ও প্রোগ্রামিং সহকারী" else "CODING & ALGORITHM ASSISTANT",
                        color = JarvisCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "এআই চালিত সিনট্যাক্স, ডিবাগিং ও আর্কিটেকচার" else "AI-Powered Syntax, Debugging & Code Generation",
                        color = JarvisTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Presets
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(presetTemplates) { item ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(JarvisSurfaceVariant)
                        .border(1.dp, JarvisSurfaceBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            codeQuery = item
                            onGenerateCode(item)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = item,
                        color = JarvisTextCyan,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Input Box
        OutlinedTextField(
            value = codeQuery,
            onValueChange = { codeQuery = it },
            placeholder = {
                Text(
                    text = if (language == JarvisLanguage.BENGALI) "আপনার কোডিং প্রশ্ন বা ফাংশন বর্ণনা লিখুন…" else "Describe the code, algorithm or bug…",
                    color = JarvisTextSecondary,
                    fontSize = 13.sp
                )
            },
            modifier = Modifier.fillMaxWidth().testTag("coding_input_field"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = JarvisTextPrimary,
                unfocusedTextColor = JarvisTextPrimary,
                focusedBorderColor = JarvisCyan,
                unfocusedBorderColor = JarvisSurfaceBorder,
                focusedContainerColor = JarvisSurface,
                unfocusedContainerColor = JarvisSurface
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4
        )

        Button(
            onClick = {
                if (codeQuery.isNotBlank()) {
                    onGenerateCode(codeQuery)
                }
            },
            enabled = codeQuery.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth().testTag("generate_code_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = JarvisCyan,
                disabledContainerColor = JarvisSurfaceVariant
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = JarvisBackground,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = if (isLoading) {
                    if (language == JarvisLanguage.BENGALI) "কোড তৈরি হচ্ছে…" else "Generating Code…"
                } else {
                    if (language == JarvisLanguage.BENGALI) "কোড তৈরি করুন" else "Generate Code"
                },
                color = JarvisBackground,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }

        // Result Output
        if (!codeOutput.isNullOrBlank()) {
            HudCard(borderColor = JarvisCyan) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "জার্ভিস কোড সমাধান:" else "JARVIS CODE SOLUTION:",
                        color = JarvisCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Render code or code blocks
                    if (codeOutput.contains("```")) {
                        val segments = codeOutput.split("```")
                        segments.forEachIndexed { i, seg ->
                            if (i % 2 == 1) {
                                val lines = seg.trim().lines()
                                val lang = if (lines.firstOrNull()?.length ?: 0 < 15) lines.firstOrNull() else "code"
                                val body = if (lines.size > 1) lines.drop(1).joinToString("\n") else seg
                                CodeBlockView(code = body, language = lang)
                            } else if (seg.isNotBlank()) {
                                Text(
                                    text = seg.trim(),
                                    color = JarvisTextPrimary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    } else {
                        CodeBlockView(code = codeOutput, language = "kotlin")
                    }
                }
            }
        }
    }
}
