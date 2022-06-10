package parallelai.common.secure.diffiehellman

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class ClientSharedSecret(value: Array[Byte])

object ClientSharedSecret {
  implicit val rootJsonFormat: RootJsonFormat[ClientSharedSecret] =
    jsonFormat1(ClientSharedSecret.apply)

  implicit val encoder: Encoder[ClientSharedSecret] = deriveEncoder

  implicit val decoder: Decoder[ClientSharedSecret] = deriveDecoder
}

case class ServerSharedSecret(value: Array[Byte])

object ServerSharedSecret {
  implicit val rootJsonFormat: RootJsonFormat[ServerSharedSecret] =
    jsonFormat1(ServerSharedSecret.apply)

  implicit val encoder: Encoder[ServerSharedSecret] = deriveEncoder

  implicit val decoder: Decoder[ServerSharedSecret] = deriveDecoder
}