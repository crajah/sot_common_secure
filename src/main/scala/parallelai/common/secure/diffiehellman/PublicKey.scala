package parallelai.common.secure.diffiehellman

import io.circe.generic.semiauto.{ deriveDecoder, deriveEncoder }
import io.circe.{ Decoder, Encoder }
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

sealed trait PublicKey {
  def value: Array[Byte]
}

case class ClientPublicKey(value: Array[Byte]) extends PublicKey

object ClientPublicKey {
  implicit val clientPublicKeyRootJsonFormat: RootJsonFormat[ClientPublicKey] =
    jsonFormat1(ClientPublicKey.apply)

  implicit val encoder: Encoder[ClientPublicKey] = deriveEncoder

  implicit val decoder: Decoder[ClientPublicKey] = deriveDecoder
}

case class ServerPublicKey(value: Array[Byte]) extends PublicKey

object ServerPublicKey {
  implicit val serverPublicKeyRootJsonFormat: RootJsonFormat[ServerPublicKey] =
    jsonFormat1(ServerPublicKey.apply)

  implicit val encoder: Encoder[ServerPublicKey] = deriveEncoder

  implicit val decoder: Decoder[ServerPublicKey] = deriveDecoder
}