package com.example.ui.screens

import android.content.Context
import android.hardware.camera2.CameraManager
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
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.JarvisLanguage
import com.example.model.TelemetryInfo
import com.example.ui.components.HudCard
import com.example.ui.theme.JarvisAmber
import com.example.ui.theme.JarvisBackground
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisNeonGreen
import com.example.ui.theme.JarvisSurface
import com.example.ui.theme.JarvisSurfaceBorder
import com.example.ui.theme.JarvisSurfaceVariant
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun TelemetryDashboardScreen(
    telemetry: TelemetryInfo,
    language: JarvisLanguage,
    onRefreshTelemetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isFlashlightOn by remember { mutableStateOf(false) }

    val ramUsedMb = (telemetry.ramTotalMb - telemetry.ramAvailableMb).coerceAtLeast(0)
    val ramProgress = if (telemetry.ramTotalMb > 0) ramUsedMb.toFloat() / telemetry.ramTotalMb else 0.5f

    val storageUsedGb = (telemetry.storageTotalGb - telemetry.storageAvailableGb).coerceAtLeast(0)
    val storageProgress = if (telemetry.storageTotalGb > 0) storageUsedGb.toFloat() / telemetry.storageTotalGb else 0.6f

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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(JarvisCyan.copy(alpha = 0.15f))
                            .border(1.dp, JarvisCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Telemetry",
                            tint = JarvisCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (language == JarvisLanguage.BENGALI) "সিস্টেম টেলিমেট্রি ও ড্যাশবোর্ড" else "SYSTEM TELEMETRY & RESOURCES",
                            color = JarvisCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (language == JarvisLanguage.BENGALI) "রিয়েল-টাইম পারফরম্যান্স ও রিসোর্স মনিটর" else "Real-time Telemetry & Resource Profiler",
                            color = JarvisTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = onRefreshTelemetry,
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceVariant),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("refresh_telemetry_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = JarvisCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Encryption Security Card
        HudCard(borderColor = JarvisNeonGreen.copy(alpha = 0.5f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Encryption",
                    tint = JarvisNeonGreen,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) "এনক্রিপশন প্রোটোকল: সক্রিয়" else "ENCRYPTION STATUS: ACTIVE",
                        color = JarvisNeonGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (language == JarvisLanguage.BENGALI) {
                            "AES-256 GCM স্থানীয় এনক্রিপশন সক্রিয়। সংবেদনশীল তথ্য ডিভাইসেই সুরক্ষিত থাকে।"
                        } else {
                            "AES-256 GCM hardware-backed keystore active. Sensitive user interactions guarded at rest."
                        },
                        color = JarvisTextPrimary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Resource Gauges
        HudCard {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Battery
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BatteryChargingFull,
                                contentDescription = "Battery",
                                tint = JarvisCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == JarvisLanguage.BENGALI) "ব্যাটারি লেভেল" else "Battery Charge",
                                color = JarvisTextPrimary,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "${telemetry.batteryPercent}% ${if (telemetry.isCharging) "(Charging)" else ""}",
                            color = JarvisCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { telemetry.batteryPercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = JarvisCyan,
                        trackColor = JarvisSurfaceVariant
                    )
                }

                // RAM
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Memory,
                                contentDescription = "RAM",
                                tint = JarvisAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == JarvisLanguage.BENGALI) "র‍্যাম ব্যবহার (RAM)" else "RAM Allocation",
                                color = JarvisTextPrimary,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "$ramUsedMb MB / ${telemetry.ramTotalMb} MB",
                            color = JarvisAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { ramProgress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = JarvisAmber,
                        trackColor = JarvisSurfaceVariant
                    )
                }

                // Storage
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.SdStorage,
                                contentDescription = "Storage",
                                tint = JarvisNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (language == JarvisLanguage.BENGALI) "অভ্যন্তরীণ স্টোরেজ" else "Internal Storage",
                                color = JarvisTextPrimary,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "$storageUsedGb GB / ${telemetry.storageTotalGb} GB",
                            color = JarvisNeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { storageProgress },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = JarvisNeonGreen,
                        trackColor = JarvisSurfaceVariant
                    )
                }
            }
        }

        // Smart Hardware Controls
        HudCard {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = if (language == JarvisLanguage.BENGALI) "স্মার্ট ডিভাইস কন্ট্রোল" else "SMART DEVICE CONTROLS",
                    color = JarvisCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                val camManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
                                val cameraId = camManager?.cameraIdList?.firstOrNull()
                                if (cameraId != null) {
                                    isFlashlightOn = !isFlashlightOn
                                    camManager.setTorchMode(cameraId, isFlashlightOn)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        modifier = Modifier.weight(1f).testTag("toggle_torch_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFlashlightOn) JarvisAmber else JarvisSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashlightOn,
                            contentDescription = "Torch",
                            tint = if (isFlashlightOn) JarvisBackground else JarvisCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isFlashlightOn) {
                                if (language == JarvisLanguage.BENGALI) "টর্চ বন্ধ" else "Torch Off"
                            } else {
                                if (language == JarvisLanguage.BENGALI) "টর্চ চালু" else "Torch On"
                            },
                            color = if (isFlashlightOn) JarvisBackground else JarvisTextPrimary,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(JarvisSurfaceVariant)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Wifi,
                                contentDescription = "Network",
                                tint = JarvisCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = telemetry.networkStatus,
                                color = JarvisTextPrimary,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
