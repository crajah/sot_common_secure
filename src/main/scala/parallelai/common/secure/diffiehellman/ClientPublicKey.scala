package parallelai.common.secure.diffiehellman

import java.security.KeyPair
import spray.json.DefaultJsonProtocol._
import spray.json._

case class ClientPublicKey(value: Array[Byte], keyPair: KeyPair) extends PublicKey

object ClientPublicKey extends PublicKeyImplicits {
  implicit val clientPublicKeyRootJsonFormat: RootJsonFormat[ClientPublicKey] =
    jsonFormat2(ClientPublicKey.apply)
}