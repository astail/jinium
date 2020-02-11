package net.astail

import com.typesafe.config.ConfigFactory
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64

import scala.util.Try
import scala.util.Random

object crypto {
  val key = ConfigFactory.load.getString("decode_key")

  private val defaultCharset = "UTF-8"
  private val aesKey = new SecretKeySpec(Base64.decodeBase64(key), "AES")

  private[this] def encrypt(xs: Array[Byte]): Array[Byte] = {
    val cipherEnc = Cipher.getInstance("AES")
    cipherEnc.init(Cipher.ENCRYPT_MODE, aesKey)
    cipherEnc.doFinal(xs)
  }

  private[this] def decrypt(xs: Array[Byte]): Array[Byte] = {
    val cipherEnc = Cipher.getInstance("AES")
    cipherEnc.init(Cipher.DECRYPT_MODE, aesKey)
    cipherEnc.doFinal(xs)
  }

  def encryptString(secret: String, separator: String): String = {
    val xs = (secret + separator + Random.nextString(8)).getBytes(defaultCharset)
    Base64.encodeBase64URLSafeString(encrypt(xs))
  }

  def decryptString(encrypted: String, separator: String): Option[String] = {
    Try {
      val s = new String(decrypt(Base64.decodeBase64(encrypted)), defaultCharset)
      val ss = s.split(separator)
      if (ss.length < 2) None
      else Some(ss(0))
    }.toOption.flatten
  }
}