package parallelai.common.secure.diffiehellman

import java.security.KeyPair
import java.util.Base64.{getDecoder, getEncoder}
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, RootJsonFormat, deserializationError}
import org.apache.commons.lang3.SerializationUtils._

sealed trait PublicKey {
  def value: Array[Byte]

  def keyPair: KeyPair
}

trait PublicKeyJsonFormat {
  implicit val keyPairRootJsonFormat: RootJsonFormat[KeyPair] = new RootJsonFormat[KeyPair] {
    def read(json: JsValue): KeyPair = json match {
      case JsString(j) => deserialize(getDecoder decode j).asInstanceOf[KeyPair]
      case _ => deserializationError("KeyPair reading error")
    }

    def write(keyPair: KeyPair): JsValue =
      new JsString(getEncoder encodeToString serialize(keyPair))
  }
}

case class ClientPublicKey(value: Array[Byte], keyPair: KeyPair) extends PublicKey

object ClientPublicKey extends PublicKeyJsonFormat {
  implicit val clientPublicKeyRootJsonFormat: RootJsonFormat[ClientPublicKey] =
    jsonFormat2(ClientPublicKey.apply)
}

case class ServerPublicKey(value: Array[Byte], keyPair: KeyPair) extends PublicKey

object ServerPublicKey extends PublicKeyJsonFormat {
  implicit val serverPublicKeyRootJsonFormat: RootJsonFormat[ServerPublicKey] =
    jsonFormat2(ServerPublicKey.apply)
}