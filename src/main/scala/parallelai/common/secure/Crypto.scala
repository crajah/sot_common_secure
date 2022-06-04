package parallelai.common.secure

import java.security.{ AlgorithmParameters, Key, MessageDigest }
import java.util.Base64
import javax.crypto._
import javax.crypto.spec.{ DESKeySpec, SecretKeySpec }
import scala.language.implicitConversions

sealed trait Algorithm {
  def name: String

  def value: String

  override def toString: String = name
}

case object HS256 extends Algorithm {
  val name = "HS256"
  val value = "HmacSHA256"
}

case object HS384 extends Algorithm {
  val name = "HS384"
  val value = "HmacSHA384"
}

case object HS512 extends Algorithm {
  val name = "HS512"
  val value = "HmacSHA512"
}

case object NONE extends Algorithm {
  val name = "NONE"
  val value = "NONE"
}

case object AES extends Algorithm {
  val name = "AES"
  val value = "AES/CBC/PKCS5Padding"
}

case object DES extends Algorithm {
  val name = "DES"
  val value = "DES/CBC/PKCS5Padding"
}

case class CryptoResult[T](payload: T, params: Option[Array[Byte]] = None)

object Algorithm {
  def apply(name: String): Algorithm = name match {
    case s if s == HS256.name => HS256
    case s if s == HS384.name => HS384
    case s if s == HS512.name => HS512
    case s if s == AES.name => AES
    case s if s == DES.name => DES
    case s if s == NONE.name => NONE
    case _ => throw new Exception("Unknown Algorithm")
  }
}

class CryptoMechanic(algorithm: Algorithm = AES, secret: Array[Byte]) extends ConversionHelper {
  private var charset: String = "utf-8"

  def setCharset(set: String): Unit = charset = set

  def getAlgorithm: Algorithm = algorithm

  def getCharset: String = charset

  def getSignature(payload: Array[Byte]): Array[Byte] = {
    require(algorithm != null, "Algorithm not set")
    require(secret != null, "Secret not set")

    algorithm match {
      case HS256 | HS384 | HS512 =>
        val mac: Mac = Mac.getInstance(algorithm.value)
        mac.init(new SecretKeySpec(secret, algorithm.value))
        mac.doFinal(payload)

      case AES | DES =>
        encrypt(payload, None).payload

      case NONE =>
        "".getBytes(charset)
    }
  }

  def getB64Signature(payload: String): String =
    Base64.getUrlEncoder.encodeToString(getSignature(payload))

  private def secretKey: Key = {
    require(secret != null)
    require(algorithm != null)

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
  }

  private def perform[I, O](msg: I, dir: CIPHER, cipherParams: Option[Array[Byte]] = None)(implicit i: I => Array[Byte], o: Array[Byte] => O): CryptoResult[O] = {
    require(algorithm match {
      case AES | DES => true
      case _ => false
    }, s"Cannot $dir with Algorithm $algorithm")

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

  def encrypt[I, O](msg: I, params: Option[Array[Byte]] = None)(implicit m: I => Array[Byte], n: Array[Byte] => O): CryptoResult[O] =
    perform(msg, ENCRYPT, params)

  def decrypt[I, O](code: I, params: Option[Array[Byte]] = None)(implicit m: I => Array[Byte], n: Array[Byte] => O): CryptoResult[O] =
    perform(code, DECRYPT, params)
}

trait ConversionHelper {
  implicit def string2Bytes(s: String): Array[Byte] = s.getBytes()

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
   * @param b Byte
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
}