package parallelai.common.secure

import java.util.Base64
import cats.implicits._
import io.circe.Decoder.Result
import io.circe._

case class Encrypted[T: ToBytes: FromBytes] private (value: Array[Byte], params: Option[Array[Byte]]) {
  def decrypt(implicit crypto: Crypto): T = Encrypted decrypt this
}

object Encrypted {
  implicit def encoder[T: Encoder]: Encoder[Encrypted[T]] =
    Encoder.forProduct2("value", "params")(e => (e.value, e.params))

  // TODO - Sort out mess
  implicit def decoder[T: Decoder: ToBytes: FromBytes]: Decoder[Encrypted[T]] = new Decoder[Encrypted[T]] {
    def apply(c: HCursor): Result[Encrypted[T]] = {
      val value = c.value.asObject.get("value").get.as[Array[Byte]].right.get
      val params = c.value.asObject.get("params").fold(none[Array[Byte]]) { _.as[Array[Byte]].right.get.some }

      Right(Encrypted[T](value, params))
    }
  }

  def apply[T: ToBytes: FromBytes](value: T)(implicit crypto: Crypto): Encrypted[T] = {
    val CryptoResult(cryptoPayload, cryptoParams) = crypto.encrypt(ToBytes[T].apply(value))

    new Encrypted(Base64.getEncoder.encode(cryptoPayload.repr), cryptoParams.map(Base64.getEncoder.encode))
  }

  def decrypt[T: FromBytes](encrypted: Encrypted[T])(implicit crypto: Crypto): T =
    FromBytes[T].apply(crypto.decrypt(Base64.getDecoder.decode(encrypted.value), encrypted.params.map(Base64.getDecoder.decode)).payload.repr)
}