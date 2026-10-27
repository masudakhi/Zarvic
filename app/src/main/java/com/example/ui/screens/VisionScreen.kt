package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CenterFocusStrong
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun VisionScreen(
    language: JarvisLanguage,
    isAnalyzing: Boolean,
    analysisResult: String?,
    onAnalyzeImage: (Bitmap, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var promptText by remember {
        mutableStateOf(
            if (language == JarvisLanguage.BENGALI) "এই ছবিতে যা আছে তা বিস্তারিতভাবে বিশ্লেষণ করুন" else "Analyze and describe what is visible in this image in detail"
        )
    }

    // Photo picker launcher (Android zero-permission photo picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                        decoder.isMutableRequired = true
                    }
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                selectedBitmap = bitmap
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Vision Scanner Card
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
                        imageVector = Icons.Default.CenterFocusStrong,
                        contentDescription = "Vision",
                        tint = JarvisCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "ক্যামেরা ও ভিশন স্ক্যানার" else "CAMERA & VISION SCANNER",
                        color = JarvisCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "জেমিনি মাল্টিমোডাল এআই পর্যবেক্ষণ" else "Gemini Multimodal Object & Text Analysis",
                        color = JarvisTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Image Viewfinder Box with Holographic Target Borders
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(JarvisSurface)
                .border(1.5.dp, JarvisCyan.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .testTag("vision_viewfinder"),
            contentAlignment = Alignment.Center
        ) {
            if (selectedBitmap != null) {
                Image(
                    bitmap = selectedBitmap!!.asImageBitmap(),
                    contentDescription = "Selected image for scan",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "No image",
                        tint = JarvisCyan.copy(alpha = 0.6f),
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "ছবি স্ক্যান করতে সিলেক্ট করুন" else "Select an image or document to scan",
                        color = JarvisTextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Cyber Target overlay corners
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(2.dp, JarvisCyan, RoundedCornerShape(topStart = 4.dp))
                        .align(Alignment.TopStart)
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(2.dp, JarvisCyan, RoundedCornerShape(topEnd = 4.dp))
                        .align(Alignment.TopEnd)
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(2.dp, JarvisCyan, RoundedCornerShape(bottomStart = 4.dp))
                        .align(Alignment.BottomStart)
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .border(2.dp, JarvisCyan, RoundedCornerShape(bottomEnd = 4.dp))
                        .align(Alignment.BottomEnd)
                )
            }
        }

        // Action Buttons: Select / Capture
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    photoPickerLauncher.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("select_photo_button"),
                colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = "Gallery",
                    tint = JarvisCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (language == JarvisLanguage.BENGALI) "ছবি নিন" else "Pick Image",
                    color = JarvisTextPrimary,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    selectedBitmap?.let { bmp ->
                        onAnalyzeImage(bmp, promptText)
                    }
                },
                enabled = selectedBitmap != null && !isAnalyzing,
                modifier = Modifier
                    .weight(1f)
                    .testTag("analyze_image_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = JarvisCyan,
                    disabledContainerColor = JarvisSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(
                        color = JarvisBackground,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = if (isAnalyzing) {
                        if (language == JarvisLanguage.BENGALI) "স্ক্যান হচ্ছে…" else "Scanning…"
                    } else {
                        if (language == JarvisLanguage.BENGALI) "এআই বিশ্লেষণ" else "Analyze AI"
                    },
                    color = if (selectedBitmap != null) JarvisBackground else JarvisTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Custom Query Field
        OutlinedTextField(
            value = promptText,
            onValueChange = { promptText = it },
            label = {
                Text(
                    text = if (language == JarvisLanguage.BENGALI) "বিশ্লেষণের নির্দেশিকা বা প্রশ্ন" else "Analysis Instructions",
                    color = JarvisTextSecondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("vision_prompt_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = JarvisTextPrimary,
                unfocusedTextColor = JarvisTextPrimary,
                focusedBorderColor = JarvisCyan,
                unfocusedBorderColor = JarvisSurfaceBorder,
                focusedContainerColor = JarvisSurface,
                unfocusedContainerColor = JarvisSurface
            ),
            shape = RoundedCornerShape(10.dp),
            maxLines = 2
        )

        // Analysis Results Card
        if (!analysisResult.isNullOrBlank()) {
            HudCard(
                borderColor = JarvisCyan,
                glowCyan = true
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "জার্ভিস ভিশন ফলাফল:" else "JARVIS VISION FINDINGS:",
                        color = JarvisCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = analysisResult,
                        color = JarvisTextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }
        }
    }
}
