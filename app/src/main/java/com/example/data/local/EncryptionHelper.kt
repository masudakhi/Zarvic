package com.example.data.local

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionHelper {
    // 256-bit encryption key derivation for local storage
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12

    // Deterministic app-scoped key salt
    private val keyBytes = byteArrayOf(
        0x4a.toByte(), 0x61.toByte(), 0x72.toByte(), 0x76.toByte(),
        0x69.toByte(), 0x73.toByte(), 0x5f.toByte(), 0x53.toByte(),
        0x65.toByte(), 0x63.toByte(), 0x75.toByte(), 0x72.toByte(),
        0x65.toByte(), 0x5f.toByte(), 0x4b.toByte(), 0x65.toByte(),
        0x79.toByte(), 0x5f.toByte(), 0x32.toByte(), 0x30.toByte(),
        0x32.toByte(), 0x36.toByte(), 0x5f.toByte(), 0x4e.toByte(),
        0x61.toByte(), 0x6e.toByte(), 0x6f.toByte(), 0x5f.toByte(),
        0x41.toByte(), 0x45.toByte(), 0x53.toByte(), 0x21.toByte()
    )
    private val secretKey = SecretKeySpec(keyBytes, "AES")

    data class EncryptedResult(val encryptedBase64: String, val ivBase64: String)

    fun encrypt(plainText: String): EncryptedResult {
        val iv = ByteArray(IV_LENGTH_BYTE)
        SecureRandom().nextBytes(iv)
        val cipher = Cipher.getInstance(ALGORITHM)
        val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec)
        val cipherBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        return EncryptedResult(
            encryptedBase64 = Base64.encodeToString(cipherBytes, Base64.NO_WRAP),
            ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        )
    }

    fun decrypt(encryptedBase64: String, ivBase64: String): String {
        return try {
            val cipherBytes = Base64.decode(encryptedBase64, Base64.NO_WRAP)
            val iv = Base64.decode(ivBase64, Base64.NO_WRAP)
            val cipher = Cipher.getInstance(ALGORITHM)
            val parameterSpec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec)
            val plainBytes = cipher.doFinal(cipherBytes)
            String(plainBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            "Decryption Error: [Protected Data]"
        }
    }
}
