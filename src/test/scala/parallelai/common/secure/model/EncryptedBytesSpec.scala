package parallelai.common.secure.model

import io.circe.syntax._
import spray.json._
import org.scalatest.{ MustMatchers, WordSpec }
import parallelai.common.secure.CryptoMechanic

class EncryptedBytesSpec extends WordSpec with MustMatchers {
  implicit val crypto: CryptoMechanic = new CryptoMechanic(secret = "victorias secret".getBytes)

  "Encrypted bytes" should {
    "encrypt and decrypt given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = EncryptedBytes(message)
      val decryptedMessage = encryptedMessage.decrypt

      new String(decryptedMessage) mustEqual message
    }

    "encrypt and convert to JSON which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = EncryptedBytes(message)
      val encryptedJson: JsValue = encryptedMessage.toJson

      val decryptedMessage = encryptedJson.convertTo[EncryptedBytes].decrypt

      new String(decryptedMessage) mustEqual message
    }

    "encrypt and convert to String which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = EncryptedBytes(message)
      val encryptedJson: JsValue = JsObject("productToken" -> encryptedMessage.toJson)
      val encryptedString = encryptedJson.prettyPrint

      val encryptedBytes = encryptedString.parseJson.asJsObject().fields("productToken").convertTo[EncryptedBytes]
      val decryptedMessage = encryptedBytes.decrypt

      new String(decryptedMessage) mustEqual message
    }

    "encrypt and convert to String which is decrypted back to original type given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = EncryptedBytes(message)
      val encryptedJson: JsValue = encryptedMessage.toJson
      val encryptedString = encryptedJson.compactPrint

      val encryptedBytes = encryptedString.parseJson.convertTo[EncryptedBytes]
      val decryptedMessage = encryptedBytes.decryptT[String]

      decryptedMessage mustEqual message
    }

    "converted to Spray JSON" in {
      val message = "Hello world"
      val encryptedMessage = EncryptedBytes(message)

      encryptedMessage.toJson.convertTo[EncryptedBytes].decryptT[String] mustEqual message
    }

    "converted to Circe JSON" in {
      val message = "Hello world"
      val encryptedMessage = EncryptedBytes(message)

      encryptedMessage.asJson.as[EncryptedBytes].right.get.decryptT[String] mustEqual message
    }
  }
}