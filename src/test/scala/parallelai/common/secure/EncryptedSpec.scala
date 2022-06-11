package parallelai.common.secure

import io.circe.syntax._
import spray.json._
import org.scalatest.{ MustMatchers, WordSpec }

class EncryptedSpec extends WordSpec with MustMatchers {
  implicit val crypto: CryptoMechanic = new CryptoMechanic(AES, secret = "victorias secret".getBytes)

  "Encrypted" should {
    "encrypt and decrypt given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = Encrypted(message)
      val decryptedMessage = encryptedMessage.decrypt

      new String(decryptedMessage) mustEqual message
    }

    "encrypt and convert to JSON which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = Encrypted(message)
      val encryptedJson: JsValue = encryptedMessage.toJson

      val decryptedMessage = encryptedJson.convertTo[Encrypted].decrypt

      new String(decryptedMessage) mustEqual message
    }

    "encrypt and convert to String which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = Encrypted(message)
      val encryptedJson: JsValue = JsObject("productToken" -> encryptedMessage.toJson)
      val encryptedString = encryptedJson.prettyPrint

      val encryptedBytes = encryptedString.parseJson.asJsObject().fields("productToken").convertTo[Encrypted]
      val decryptedMessage = encryptedBytes.decrypt

      new String(decryptedMessage) mustEqual message
    }

    "encrypt and convert to String which is decrypted back to original type given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = Encrypted(message)
      val encryptedJson: JsValue = encryptedMessage.toJson
      val encryptedString = encryptedJson.compactPrint

      val encryptedBytes = encryptedString.parseJson.convertTo[Encrypted]
      val decryptedMessage = encryptedBytes.decryptT[String]

      decryptedMessage mustEqual message
    }

    "converted to Spray JSON" in {
      val message = "Hello world"
      val encryptedMessage = Encrypted(message)

      encryptedMessage.toJson.convertTo[Encrypted].decryptT[String] mustEqual message
    }

    "converted to Circe JSON" in {
      val message = "Hello world"
      val encryptedMessage = Encrypted(message)

      encryptedMessage.asJson.as[Encrypted].right.get.decryptT[String] mustEqual message
    }
  }
}