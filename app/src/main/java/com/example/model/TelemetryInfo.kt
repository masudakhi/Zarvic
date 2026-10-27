package com.example.model

data class TelemetryInfo(
    val batteryPercent: Int = 100,
    val isCharging: Boolean = false,
    val ramAvailableMb: Long = 0,
    val ramTotalMb: Long = 0,
    val storageAvailableGb: Long = 0,
    val storageTotalGb: Long = 0,
    val networkStatus: String = "WiFi • Connected",
    val encryptionActive: Boolean = true,
    val aiEngine: String = "Gemini 3.8 Flash • Hybrid Local Vault",
    val cpuSimulationPercent: Int = 24
)
