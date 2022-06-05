package parallelai.common.secure.model

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import parallelai.common.secure.{ CryptoMechanic, CryptoResult }

case class EncryptedBytes private (private val value: Array[Byte], private val params: Option[Array[Byte]]) {
  def decrypt(implicit crypto: CryptoMechanic): Array[Byte] = EncryptedBytes decrypt this

  def decryptT[T: FromBytes](implicit crypto: CryptoMechanic): T = EncryptedBytes decryptT this
}

object EncryptedBytes {
  implicit val rootJsonFormat: RootJsonFormat[EncryptedBytes] = jsonFormat2(EncryptedBytes.apply)

  def apply[T: ToBytes](value: T)(implicit crypto: CryptoMechanic): EncryptedBytes = {
    val CryptoResult(cryptoPayload, cryptoParams) = crypto.encrypt(ToBytes[T].apply(value))

    new EncryptedBytes(cryptoPayload.repr, cryptoParams)
  }

  def decrypt(encryptedBytes: EncryptedBytes)(implicit crypto: CryptoMechanic): Array[Byte] =
    crypto.decrypt(encryptedBytes.value, encryptedBytes.params).payload.repr

  def decryptT[T: FromBytes](encryptedBytes: EncryptedBytes)(implicit crypto: CryptoMechanic): T =
    FromBytes[T].apply(crypto.decrypt(encryptedBytes.value, encryptedBytes.params).payload.repr)
}