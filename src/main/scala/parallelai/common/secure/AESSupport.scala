package parallelai.common.secure

import java.security.AlgorithmParameters
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

import scala.util.Try

case class AesEncoded(encoded: Array[Byte], params: Array[Byte])

final class AesMechanic(secret: Array[Byte], encodedParams: Option[Array[Byte]] = None) {
  // Create AES Key
  private val localAesKey = new SecretKeySpec(secret, 0, 16, "AES")
  // Create the AES Cipher
  private val localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

  encodedParams match {
    case None => localCipher.init(Cipher.ENCRYPT_MODE, localAesKey)
    case Some(ep) => {
      val aesParams = AlgorithmParameters.getInstance("AES")
      aesParams.init(ep)
      localCipher.init(Cipher.DECRYPT_MODE, localAesKey, aesParams)
    }
  }

  def encrypt(clear: Array[Byte]): Try[AesEncoded] = Try {
    val encoded = localCipher.doFinal(clear)
    val params = localCipher.getParameters.getEncoded

    AesEncoded(encoded, params)
  }

  def decrypt(ciphertext: Array[Byte]): Try[Array[Byte]] = Try {
    localCipher.doFinal(ciphertext)
  }
}
