package parallelai.common.secure

import java.nio.charset.{Charset, StandardCharsets}
import java.security.AlgorithmParameters
import java.util.Base64
import javax.crypto._
import javax.crypto.spec.SecretKeySpec

class CryptoMechanic(algorithm: Algorithm, secret: Array[Byte], val charset: Charset = StandardCharsets.UTF_8) extends Crypto {
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

  private def perform[I, O](msg: I, dir: CIPHER, cipherParams: Option[Array[Byte]] = None)(implicit i: I => Array[Byte], o: Array[Byte] => O): CryptoResult[O] = {
    val cipher = Cipher.getInstance(algorithm.value)

    cipherParams match {
      case None =>
        cipher.init(dir.mode, secretKey(algorithm, secret))

      case Some(p) =>
        val algoParms = AlgorithmParameters.getInstance(algorithm.name)
        algoParms.init(p)
        cipher.init(dir.mode, secretKey(algorithm, secret), algoParms)
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