package parallelai.common.secure

import java.security.{AlgorithmParameters, Key}
import java.util.Base64
import javax.crypto._
import javax.crypto.spec.{DESKeySpec, SecretKeySpec}

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