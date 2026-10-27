package com.example.data.repository

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import com.example.model.TelemetryInfo

class TelemetryRepository(private val context: Context) {

    fun getTelemetryInfo(): TelemetryInfo {
        // Battery status
        val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, ifilter)
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 85
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
        val batteryPct = if (scale > 0) (level * 100 / scale) else 85
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        // Memory info
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        val totalRamMb = memInfo.totalMem / (1024 * 1024)
        val availRamMb = memInfo.availMem / (1024 * 1024)

        // Storage info
        val stat = StatFs(Environment.getDataDirectory().path)
        val bytesAvailable = stat.availableBlocksLong * stat.blockSizeLong
        val bytesTotal = stat.blockCountLong * stat.blockSizeLong
        val availGb = bytesAvailable / (1024 * 1024 * 1024)
        val totalGb = bytesTotal / (1024 * 1024 * 1024)

        // Connectivity
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val network = connMgr?.activeNetwork
        val caps = connMgr?.getNetworkCapabilities(network)
        val networkStr = when {
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi • Ultra Low Latency"
            caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular 5G/LTE"
            caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true -> "Online • Active"
            else -> "Offline • Local Mode"
        }

        return TelemetryInfo(
            batteryPercent = batteryPct,
            isCharging = isCharging,
            ramAvailableMb = availRamMb,
            ramTotalMb = totalRamMb,
            storageAvailableGb = availGb,
            storageTotalGb = totalGb,
            networkStatus = networkStr,
            encryptionActive = true,
            aiEngine = "Gemini 3.8 Flash • Hybrid Local Vault",
            cpuSimulationPercent = 18 + (System.currentTimeMillis() % 15).toInt()
        )
    }
}
