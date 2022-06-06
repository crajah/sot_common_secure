package parallelai.common.secure.model

import io.circe.{ Decoder, Encoder }
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import parallelai.common.secure.{ CryptoMechanic, CryptoResult }

case class Encrypted private (private val value: Array[Byte], private val params: Option[Array[Byte]]) {
  def decrypt(implicit crypto: CryptoMechanic): Array[Byte] = Encrypted decrypt this

  def decryptT[T: FromBytes](implicit crypto: CryptoMechanic): T = Encrypted decryptT this
}

object Encrypted {
  implicit val rootJsonFormat: RootJsonFormat[Encrypted] = jsonFormat2(Encrypted.apply)

  implicit val encoder: Encoder[Encrypted] =
    Encoder.forProduct2("value", "params")(e => (e.value, e.params))

  implicit val decoder: Decoder[Encrypted] =
    Decoder.forProduct2("value", "params")(Encrypted.apply)

  def apply[T: ToBytes](value: T)(implicit crypto: CryptoMechanic): Encrypted = {
    val CryptoResult(cryptoPayload, cryptoParams) = crypto.encrypt(ToBytes[T].apply(value))

    new Encrypted(cryptoPayload.repr, cryptoParams)
  }

  def decrypt(encrypted: Encrypted)(implicit crypto: CryptoMechanic): Array[Byte] =
    crypto.decrypt(encrypted.value, encrypted.params).payload.repr

  def decryptT[T: FromBytes](encrypted: Encrypted)(implicit crypto: CryptoMechanic): T =
    FromBytes[T].apply(crypto.decrypt(encrypted.value, encrypted.params).payload.repr)
}