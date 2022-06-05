package parallelai.common.secure.model

import org.scalatest.TryValues._
import org.scalatest.{ MustMatchers, WordSpec }
import parallelai.common.secure.CryptoMechanic
import spray.json._

class EncryptedBytesSpec extends WordSpec with MustMatchers {
  implicit val crypto: CryptoMechanic = new CryptoMechanic(secret = "victorias secret".getBytes)

  "Encrypted bytes" should {
    "encrypt and decrypt given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = EncryptedBytes(message).success.value
      new String(encryptedMessage.decrypt.success.value) mustEqual message
    }

    "encrypt and convert to JSON which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage: EncryptedBytes = EncryptedBytes(message).success.value
      val encryptedJson: JsValue = encryptedMessage.toJson

      val encryptedBytes = encryptedJson.convertTo[EncryptedBytes]
      new String(encryptedBytes.decrypt.success.value) mustEqual message
    }

    "encrypt and convert to String which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage: EncryptedBytes = EncryptedBytes(message).success.value
      val encryptedJson: JsValue = encryptedMessage.toJson
      val encryptedString = encryptedJson.compactPrint
      println(encryptedString)

      val fromEmailEncryptedJson: JsValue = encryptedString.parseJson
      val encryptedBytes = fromEmailEncryptedJson.convertTo[EncryptedBytes]

      new String(encryptedBytes.decrypt.success.value) mustEqual message
    }
  }
}