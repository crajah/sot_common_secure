package parallelai.common.secure.diffiehellman

import java.security.KeyPair
import spray.json.DefaultJsonProtocol._
import spray.json._

case class ServerPublicKey(value: Array[Byte], keyPair: KeyPair) extends PublicKey

object ServerPublicKey extends PublicKeyImplicits {
  implicit val serverPublicKeyRootJsonFormat: RootJsonFormat[ServerPublicKey] =
    jsonFormat2(ServerPublicKey.apply)
}