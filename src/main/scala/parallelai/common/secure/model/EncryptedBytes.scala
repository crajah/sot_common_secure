package parallelai.common.secure.model

import scala.util.Try
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import parallelai.common.secure.{ CryptoMechanic, CryptoResult }

case class EncryptedBytes private (private val value: Array[Byte], private val params: Option[Array[Byte]]) {
  def decrypt(implicit crypto: CryptoMechanic): Try[Array[Byte]] = EncryptedBytes decrypt this

  def decryptT[T: FromBytes](implicit crypto: CryptoMechanic): Try[T] = EncryptedBytes decryptT this
}

object EncryptedBytes {
  implicit val rootJsonFormat: RootJsonFormat[EncryptedBytes] = jsonFormat2(EncryptedBytes.apply)

  def apply[T: ToBytes](value: T)(implicit crypto: CryptoMechanic) = Try {
    val cryptoResult: CryptoResult[Array[Byte]] = crypto.encrypt(ToBytes[T].apply(value))

    new EncryptedBytes(cryptoResult.payload.repr, cryptoResult.params)
  }

  def decrypt(encryptedBytes: EncryptedBytes)(implicit crypto: CryptoMechanic): Try[Array[Byte]] = Try {
    crypto.decrypt(encryptedBytes.value, encryptedBytes.params).payload.repr
  }

  def decryptT[T: FromBytes](encryptedBytes: EncryptedBytes)(implicit crypto: CryptoMechanic): Try[T] = Try {
    FromBytes[T].apply(crypto.decrypt(encryptedBytes.value, encryptedBytes.params).payload.repr)
  }
}