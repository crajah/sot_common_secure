package parallelai.common.secure.diffiehellman

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

case class ClientSharedSecret(value: Array[Byte])

object ClientSharedSecret {
  implicit val rootJsonFormat: RootJsonFormat[ClientSharedSecret] =
    jsonFormat1(ClientSharedSecret.apply)
}

case class ServerSharedSecret(value: Array[Byte])

object ServerSharedSecret {
  implicit val rootJsonFormat: RootJsonFormat[ServerSharedSecret] =
    jsonFormat1(ServerSharedSecret.apply)
}