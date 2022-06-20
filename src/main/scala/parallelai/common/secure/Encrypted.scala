package parallelai.common.secure

import java.util.Base64
import cats.implicits._
import io.circe.Decoder.Result
import io.circe._
import org.apache.commons.lang3.SerializationUtils.{deserialize, serialize}

case class Encrypted[T: ToBytes: FromBytes] private (value: Array[Byte], params: Option[Array[Byte]]) {
  def decrypt(implicit crypto: Crypto): T = Encrypted decrypt this

  def toBytes: Array[Byte] = Encrypted toBytes this
}

object Encrypted {
  implicit def encryptedToBytes[T: ToBytes]: ToBytes[Encrypted[T]] = new ToBytes[Encrypted[T]] {
    def apply(t: Encrypted[T]): Array[Byte] = serialize((t.value, t.params))
  }

  implicit def encryptedFromBytes[T: ToBytes: FromBytes]: FromBytes[Encrypted[T]] = new FromBytes[Encrypted[T]] {
    def apply(a: Array[Byte]): Encrypted[T] = {
      val (value, params) = deserialize[(Array[Byte], Option[Array[Byte]])](a)
      Encrypted(value, params)
    }
  }

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

  def apply[T: ToBytes: FromBytes](value: T, crypto: Crypto): Encrypted[T] = {
    implicit val cryptoImplicit: Crypto = crypto
    apply(value)
  }

  def decrypt[T: FromBytes](encrypted: Encrypted[T])(implicit crypto: Crypto): T =
    FromBytes[T].apply(crypto.decrypt(Base64.getDecoder.decode(encrypted.value), encrypted.params.map(Base64.getDecoder.decode)).payload.repr)

  def toBytes[T: ToBytes: FromBytes](t: Encrypted[T]): Array[Byte] =
    encryptedToBytes[T].apply(t)

  def fromBytes[T: ToBytes: FromBytes](a: Array[Byte]) =
    encryptedFromBytes[T].apply(a)
}