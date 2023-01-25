package parallelai.common.secure.diffiehellman

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat
import org.apache.commons.lang3.SerializationUtils.{deserialize, serialize}
import parallelai.common.secure.{FromBytes, ToBytes}

sealed trait PublicKey {
  def value: Array[Byte]
}

case class ClientPublicKey(value: Array[Byte]) extends PublicKey

object ClientPublicKey {
  implicit val clientPublicKeyRootJsonFormat: RootJsonFormat[ClientPublicKey] =
    jsonFormat1(ClientPublicKey.apply)

  implicit val encoder: Encoder[ClientPublicKey] = deriveEncoder

  implicit val decoder: Decoder[ClientPublicKey] = deriveDecoder

  implicit val toBytes: ToBytes[ClientPublicKey] = new ToBytes[ClientPublicKey] {
    def apply(clientPublicKey: ClientPublicKey): Array[Byte] = serialize(clientPublicKey)
  }

  implicit val fromBytes: FromBytes[ClientPublicKey] = new FromBytes[ClientPublicKey] {
    def apply(as: Array[Byte]): ClientPublicKey = deserialize[ClientPublicKey](as)
  }
}

case class ServerPublicKey(value: Array[Byte]) extends PublicKey

object ServerPublicKey {
  implicit val serverPublicKeyRootJsonFormat: RootJsonFormat[ServerPublicKey] =
    jsonFormat1(ServerPublicKey.apply)

  implicit val encoder: Encoder[ServerPublicKey] = deriveEncoder

  implicit val decoder: Decoder[ServerPublicKey] = deriveDecoder

  implicit val toBytes: ToBytes[ServerPublicKey] = new ToBytes[ServerPublicKey] {
    def apply(serverPublicKey: ServerPublicKey): Array[Byte] = serialize(serverPublicKey)
  }

  implicit val fromBytes: FromBytes[ServerPublicKey] = new FromBytes[ServerPublicKey] {
    def apply(as: Array[Byte]): ServerPublicKey = deserialize[ServerPublicKey](as)
  }
}