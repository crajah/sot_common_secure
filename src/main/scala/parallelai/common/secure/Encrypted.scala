package parallelai.common.secure

import cats.implicits._
import io.circe.Decoder.Result
import io.circe._

case class Encrypted[T: ToBytes: FromBytes] private (private val value: Array[Byte], private val params: Option[Array[Byte]]) {
  def decrypt(implicit crypto: CryptoMechanic): T = Encrypted decrypt this
}

object Encrypted {
  // TODO
  // implicit def rootJsonFormat[T: ToBytes: FromBytes]: RootJsonFormat[Encrypted[T]] = jsonFormat2(Encrypted.apply)

  implicit def encoder[T: Encoder]: Encoder[Encrypted[T]] =
    Encoder.forProduct2("value", "params")(e => (e.value, e.params))

  // TODO - Sort out mess
  implicit def decoder[T: Decoder: ToBytes: FromBytes]: Decoder[Encrypted[T]] = new Decoder[Encrypted[T]] {
    def apply(c: HCursor): Result[Encrypted[T]] = {
      val value = c.top.get.asObject.get("value").get.as[Array[Byte]].right.get
      val params = c.top.get.asObject.get("params").fold(none[Array[Byte]]) { _.as[Array[Byte]].right.get.some }

      Right(Encrypted[T](value, params))
    }
  }

  def apply[T: ToBytes: FromBytes](value: T)(implicit crypto: CryptoMechanic): Encrypted[T] = {
    val CryptoResult(cryptoPayload, cryptoParams) = crypto.encrypt(ToBytes[T].apply(value))

    new Encrypted(cryptoPayload.repr, cryptoParams)
  }

  def decrypt[T: FromBytes](encrypted: Encrypted[T])(implicit crypto: CryptoMechanic): T =
    FromBytes[T].apply(crypto.decrypt(encrypted.value, encrypted.params).payload.repr)
}