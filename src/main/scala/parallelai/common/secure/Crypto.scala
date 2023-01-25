package parallelai.common.secure

import java.nio.charset.{Charset, StandardCharsets}
import java.security.{AlgorithmParameters, Key, MessageDigest, SecureRandom}
import java.util.Base64

import scala.language.implicitConversions
import javax.crypto._
import javax.crypto.spec.{DESKeySpec, IvParameterSpec, SecretKeySpec}

object Crypto extends CryptoUtil {
  def apply(algorithm: Algorithm, secret: Array[Byte], charset: Charset = StandardCharsets.UTF_8, randomIv: Boolean = false) =
    new Crypto(algorithm, secret, charset, randomIv)
}

class Crypto(algorithm: Algorithm, secret: Array[Byte], val charset: Charset = StandardCharsets.UTF_8, val randomIv: Boolean = false) extends CryptoUtil {
  def encrypt[I, O](msg: I, params: Option[Array[Byte]] = None)(implicit m: I => Array[Byte], n: Array[Byte] => O): CryptoResult[O] =
    perform(msg, ENCRYPT, params)

  def decrypt[I, O](code: I, params: Option[Array[Byte]] = None)(implicit m: I => Array[Byte], n: Array[Byte] => O): CryptoResult[O] =
    perform(code, DECRYPT, params)

  def getAlgorithm: Algorithm = algorithm

  def getSignature(payload: Array[Byte]): Array[Byte] = {
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
      case None => if (randomIv) cipher.init(dir.mode, secretKey)
      else cipher.init(dir.mode, secretKey, new IvParameterSpec(" " * (algorithm match {
        case AES => 16
        case DES => 8
        case _ => 0
      })))

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

