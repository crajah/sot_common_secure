package parallelai.common.secure

import io.circe.Decoder.Result
import io.circe._
import io.circe.syntax._
import spray.json.DefaultJsonProtocol._
import spray.json._
import org.apache.commons.lang3.SerializationUtils.{deserialize, serialize}

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

case class Encrypteds(values: Map[String, Encrypted]) {
  def apply(key: String): Encrypted = values(key)

  def get(key: String): Option[Encrypted] = values get key
}

object Encrypteds {
  implicit val encryptedsToBytes: ToBytes[Encrypteds] = new ToBytes[Encrypteds] {
    def apply(encrypteds: Encrypteds): Array[Byte] = serialize(encrypteds)
  }

  implicit val encryptedsFromBytes: FromBytes[Encrypteds] = new FromBytes[Encrypteds] {
    def apply(as: Array[Byte]): Encrypteds = deserialize(as).asInstanceOf[Encrypteds]
  }

  implicit val rootJsonFormat: RootJsonFormat[Encrypteds] = new RootJsonFormat[Encrypteds] {
    def write(encrypteds: Encrypteds): JsValue = encrypteds.values.toJson

    def read(json: JsValue): Encrypteds = Encrypteds(json.asJsObject.fields.map { case (k, v) =>
      k -> v.convertTo[Encrypted]
    })
  }

  implicit val encoder: Encoder[Encrypteds] = new Encoder[Encrypteds] {
    def apply(encrypteds: Encrypteds): Json = encrypteds.values.asJson
  }

  implicit val decoder: Decoder[Encrypteds] = new Decoder[Encrypteds] {
    def apply(c: HCursor): Result[Encrypteds] =
      Right(Encrypteds(c.value.asObject.get.toMap.map { case (k, j) =>
        k -> j.as[Encrypted].right.get
      }))
  }

  def apply(values: (String, Encrypted)*) = new Encrypteds(values.toMap)
}