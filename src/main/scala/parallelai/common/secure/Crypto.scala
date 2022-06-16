package parallelai.common.secure

import java.nio.charset.{Charset, StandardCharsets}
import java.security.{AlgorithmParameters, Key, MessageDigest, SecureRandom}
import java.util.Base64
import scala.language.implicitConversions
import javax.crypto._
import javax.crypto.spec.{DESKeySpec, SecretKeySpec}

object Crypto extends CryptoUtil {
  def apply(algorithm: Algorithm, secret: Array[Byte], charset: Charset = StandardCharsets.UTF_8) =
    new Crypto(algorithm, secret, charset)
}

class Crypto(algorithm: Algorithm, secret: Array[Byte], val charset: Charset = StandardCharsets.UTF_8) extends CryptoUtil {
  def encrypt[I, O](msg: I, params: Option[Array[Byte]] = None)(implicit m: I => Array[Byte], n: Array[Byte] => O): CryptoResult[O] =
    perform(msg, ENCRYPT, params)

  def decrypt[I, O](code: I, params: Option[Array[Byte]] = None)(implicit m: I => Array[Byte], n: Array[Byte] => O): CryptoResult[O] =
    perform(code, DECRYPT, params)

  def getAlgorithm: Algorithm = algorithm

  def getSignature(payload: Array[Byte]): Array[Byte] =
    algorithm match {
      case HS256 | HS384 | HS512 =>
        val mac: Mac = Mac.getInstance(algorithm.value)
        mac.init(new SecretKeySpec(secret, algorithm.value))
        mac.doFinal(payload)

      case AES | DES =>
        encrypt(payload, None).payload

      case NONE =>
        "" getBytes charset
    }

  def getB64Signature(payload: String): String =
    Base64.getUrlEncoder.encodeToString(getSignature(payload))

  private def secretKey: Key =
    algorithm match {
      case AES =>
        new SecretKeySpec(secret, 0, 16, algorithm.name)

      case DES =>
        val keySpec = new DESKeySpec(secret)
        SecretKeyFactory.getInstance(algorithm.name).generateSecret(keySpec)

      case _ => new Key {
        override def getEncoded: Array[Byte] = secret

        override def getAlgorithm: String = algorithm.name

        override def getFormat: String = algorithm.value
      }
    }

  private def perform[I, O](msg: I, dir: CIPHER, cipherParams: Option[Array[Byte]] = None)(implicit i: I => Array[Byte], o: Array[Byte] => O): CryptoResult[O] = {
    val cipher = Cipher.getInstance(algorithm.value)

    cipherParams match {
      case None =>
        cipher.init(dir.mode, secretKey)

      case Some(p) =>
        val algoParms = AlgorithmParameters.getInstance(algorithm.name)
        algoParms.init(p)
        cipher.init(dir.mode, secretKey, algoParms)
    }

    val payload = cipher.doFinal(msg)
    val params = cipher.getParameters.getEncoded

    CryptoResult[O](payload, Some(params))
  }
}

sealed trait CryptoUtil {
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