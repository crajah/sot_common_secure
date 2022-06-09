package parallelai.common.secure.diffiehellman

import spray.json.DefaultJsonProtocol._
import spray.json.RootJsonFormat

sealed trait PublicKey {
  def value: Array[Byte]
}

case class ClientPublicKey(value: Array[Byte]) extends PublicKey

object ClientPublicKey {
  implicit val clientPublicKeyRootJsonFormat: RootJsonFormat[ClientPublicKey] =
    jsonFormat1(ClientPublicKey.apply)
}

case class ServerPublicKey(value: Array[Byte]) extends PublicKey

object ServerPublicKey {
  implicit val serverPublicKeyRootJsonFormat: RootJsonFormat[ServerPublicKey] =
    jsonFormat1(ServerPublicKey.apply)
}