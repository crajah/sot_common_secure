package parallelai.common.secure.diffiehellman

import java.security.KeyPair
import java.util.Base64.{ getDecoder, getEncoder }
import spray.json.{ JsString, JsValue, RootJsonFormat, deserializationError }
import org.apache.commons.lang3.SerializationUtils._

trait PublicKey {
  def value: Array[Byte]

  def keyPair: KeyPair
}

trait PublicKeyImplicits {
  implicit val keyPairRootJsonFormat: RootJsonFormat[KeyPair] = new RootJsonFormat[KeyPair] {
    def read(json: JsValue): KeyPair = json match {
      case JsString(j) => deserialize(getDecoder decode j).asInstanceOf[KeyPair]
      case _ => deserializationError("KeyPair reading error")
    }

    def write(keyPair: KeyPair): JsValue =
      new JsString(getEncoder encodeToString serialize(keyPair))
  }
}