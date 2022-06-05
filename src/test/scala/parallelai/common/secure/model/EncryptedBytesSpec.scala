package parallelai.common.secure.model

import scala.util.Success
import spray.json._
import org.scalatest.{ MustMatchers, WordSpec }
import parallelai.common.secure.CryptoMechanic

class EncryptedBytesSpec extends WordSpec with MustMatchers {
  implicit val crypto: CryptoMechanic = new CryptoMechanic(secret = "victorias secret".getBytes)

  "Encrypted bytes" should {
    "encrypt and decrypt given some Crypto mechanism" in {
      val message = "Hello world"

      val Success(encryptedMessage) = EncryptedBytes(message)
      val Success(decryptedMessage) = encryptedMessage.decrypt

      new String(decryptedMessage) mustEqual message
    }

    "encrypt and convert to JSON which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val Success(encryptedMessage) = EncryptedBytes(message)
      val encryptedJson: JsValue = encryptedMessage.toJson

      val Success(decryptedMessage) = encryptedJson.convertTo[EncryptedBytes].decrypt

      new String(decryptedMessage) mustEqual message
    }

    "encrypt and convert to String which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val Success(encryptedMessage) = EncryptedBytes(message)
      val encryptedJson: JsValue = encryptedMessage.toJson
      val encryptedString = encryptedJson.compactPrint

      val encryptedBytes = encryptedString.parseJson.convertTo[EncryptedBytes]
      val Success(decryptedMessage) = encryptedBytes.decrypt

      new String(decryptedMessage) mustEqual message
    }

    "encrypt and convert to String which is decrypted back to original type given some Crypto mechanism" in {
      val message = "Hello world"

      val Success(encryptedMessage) = EncryptedBytes(message)
      val encryptedJson: JsValue = encryptedMessage.toJson
      val encryptedString = encryptedJson.compactPrint
      println(encryptedString)

      val encryptedBytes = encryptedString.parseJson.convertTo[EncryptedBytes]
      val Success(decryptedMessage: String) = encryptedBytes.decryptT

      decryptedMessage mustEqual message
    }
  }
}