package parallelai.common.secure

import java.security.{MessageDigest, SecureRandom}
import java.util.Base64

import javax.crypto.{KeyGenerator, SecretKey}

trait CryptoUtil {
  implicit def string2Bytes(s: String): Array[Byte] = s.getBytes

  implicit def bytes2String(b: Array[Byte]): String = new String(b)

  def toB64[I, O](bytes: I)(implicit i: I => Array[Byte], o: Array[Byte] => O): O =
    Base64.getEncoder encode bytes

  def fromB64[I, O](b64: I)(implicit i: I => Array[Byte], o: Array[Byte] => O): O =
    Base64.getDecoder decode b64

  def toB64String[I](bytes: I)(implicit i: I => Array[Byte]): String =
    Base64.getEncoder encodeToString bytes

  def fromB64String[O](b64: String)(implicit o: Array[Byte] => O): O =
    Base64.getDecoder decode b64

  def toSHA256[I, O](s: I)(implicit i: I => Array[Byte], o: Array[Byte] => O): O =
    MessageDigest getInstance "SHA-256" digest s

  def toSHA1[I, O](s: I)(implicit i: I => Array[Byte], o: Array[Byte] => O): O =
    MessageDigest getInstance "SHA-1" digest s

  def getFirstNBytes(a: Array[Byte], n: Int = 16): Array[Byte] = {
    val o = new Array[Byte](n)
    Array.copy(a, 0, o, 0, n)
    o
  }

  /**
    * Converts a byte to hex digit and writes to the supplied buffer
    *
    * @param b   Byte
    * @param buf StringBuffer
    */
  def byte2hex(b: Byte, buf: StringBuffer): Unit = {
    val hexChars = Array('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    val high = (b & 0xf0) >> 4
    val low = b & 0x0f
    buf.append(hexChars(high))
    buf.append(hexChars(low))
  }

  /**
    * Converts a byte array to hex string
    *
    * @param block Array[Byte]
    * @return String
    */
  def toHexString(block: Array[Byte]): String = {
    val buf = new StringBuffer
    val len = block.length
    var i = 0

    while (i < len) {
      byte2hex(block(i), buf)

      if (i < len - 1) buf.append(":")

      i += 1
      i - 1
    }

    buf.toString
  }

  /**
    * Randomly generated AES secret key
    *
    * @return SecretKey
    */
  def aesSecretKey: SecretKey = {
    val keyGenerator = KeyGenerator.getInstance(AES.name)
    keyGenerator.init(256, new SecureRandom)

    keyGenerator.generateKey
  }
}

object CryptoUtil extends CryptoUtil
