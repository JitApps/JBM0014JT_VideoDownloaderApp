package com.kraaft.video.manager.utils

import android.util.Base64
import java.security.Key
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Singleton

@Singleton
class EasyAES @JvmOverloads constructor(key: String, bit: Int = 256, iv: String? = null) {

    private var key: Key = if (bit == 256) {
        SecretKeySpec(getHash("SHA-256", key), ALGORITHM)
    } else {
        SecretKeySpec(getHash("MD5", key), ALGORITHM)
    }
    private var iv: IvParameterSpec = if (iv != null) {
        IvParameterSpec(getHash("MD5", iv))
    } else {
        DEFAULT_IV
    }
    private var cipher: Cipher? = null

    init {
        initCipher()
    }

    private fun initCipher() {
        try {
            cipher = Cipher.getInstance(TRANSFORMATION)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun encrypt(str: String): String {
        try {
            return encrypt(str.toByteArray(charset("UTF-8")))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }

    fun encrypt(data: ByteArray): String {
        try {
            cipher?.init(Cipher.ENCRYPT_MODE, key, iv)
            val encryptData = cipher?.doFinal(data)
            return String(Base64.encode(encryptData, Base64.DEFAULT), charset("UTF-8"))
        } catch (ex: Exception) {
            throw RuntimeException(ex.message)
        }
    }

    fun decrypt(str: String): String {
        try {
            return decrypt(Base64.decode(str, Base64.DEFAULT))
        } catch (ex: Exception) {
            throw RuntimeException(ex.message)
        }
    }

    fun decrypt(data: ByteArray): String {
        try {
            cipher?.init(Cipher.DECRYPT_MODE, key, iv)
            val decryptData = cipher?.doFinal(data)
            return String(decryptData!!, charset("UTF-8"))
        } catch (ex: Exception) {
            throw RuntimeException(ex.message)
        }
    }


    companion object {
        private val DEFAULT_IV =
            IvParameterSpec(byteArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0))
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS5Padding"

        private fun getHash(algorithm: String, text: String): ByteArray {
            try {
                return getHash(algorithm, text.toByteArray(charset("UTF-8")))
            } catch (ex: Exception) {
                throw RuntimeException(ex.message)
            }
        }

        private fun getHash(algorithm: String, data: ByteArray): ByteArray {
            try {
                val digest = MessageDigest.getInstance(algorithm)
                digest.update(data)
                return digest.digest()
            } catch (ex: Exception) {
                throw RuntimeException(ex.message)
            }
        }

        fun encryptString(content: String): String {
            return EasyAES("", 256, "").encrypt(content)
        }

        fun decryptString(content: String): String {
            try {
                return EasyAES("", 256, "").decrypt(content)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return ""
        }
    }
}