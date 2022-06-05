package parallelai.common.secure.model

import scala.util.Try
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import parallelai.common.secure.{ CryptoMechanic, CryptoResult }

case class EncryptedBytes private (private val value: Array[Byte], private val params: Option[Array[Byte]]) {
  def decrypt(implicit crypto: CryptoMechanic): Try[Array[Byte]] = EncryptedBytes decrypt this
}

object EncryptedBytes {
  implicit val rootJsonFormat: RootJsonFormat[EncryptedBytes] = jsonFormat2(EncryptedBytes.apply)

  def apply[T: ArrayOfBytes](value: T)(implicit crypto: CryptoMechanic) = Try {
    val cryptoResult: CryptoResult[Array[Byte]] = crypto.encrypt(ArrayOfBytes[T].apply(value))
    new EncryptedBytes(cryptoResult.payload.repr, cryptoResult.params)
  }

  def decrypt(encryptedBytes: EncryptedBytes)(implicit crypto: CryptoMechanic): Try[Array[Byte]] = Try {
    crypto.decrypt(encryptedBytes.value, encryptedBytes.params).payload.repr
  }
}