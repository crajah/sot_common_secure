package parallelai.common.secure

import io.circe.Json
import io.circe.parser._
import io.circe.syntax._
import org.scalatest.{Inside, MustMatchers, WordSpec}

class EncryptedSpec extends WordSpec with MustMatchers with Inside {
  implicit val crypto: CryptoMechanic = new CryptoMechanic(AES, secret = "victorias secret".getBytes)

  "Encrypted" should {
    "encrypt and decrypt given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = Encrypted[String](message)
      val decryptedMessage = encryptedMessage.decrypt

      decryptedMessage mustEqual message
    }

    "converted to Circe JSON" in {
      val message = "Hello world"
      val encryptedMessage = Encrypted(message)

      encryptedMessage.asJson.as[Encrypted[String]].right.get.decrypt mustEqual message
    }

    // TODO
    /*"converted to Spray JSON" in {
      val message = "Hello world"
      val encryptedMessage = Encrypted(message)

      encryptedMessage.toJson.convertTo[Encrypted].decryptT[String] mustEqual message
    }*/

    "encrypt and convert to JSON which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = Encrypted(message)
      val encryptedJson: Json = encryptedMessage.asJson

      val decryptedMessage = encryptedJson.as[Encrypted[String]].right.get.decrypt

      decryptedMessage mustEqual message
    }

    "encrypt and convert to String which is decrypted given some Crypto mechanism" in {
      val message = "Hello world"

      val encryptedMessage = Encrypted(message)
      val Right(encryptedString) = parse(encryptedMessage.asJson.spaces4).right.get.as[Encrypted[String]]

      val decryptedMessage = encryptedString.decrypt

      decryptedMessage mustEqual message
    }
  }
}