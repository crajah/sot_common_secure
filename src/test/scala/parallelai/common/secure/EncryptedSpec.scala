package parallelai.common.secure

import io.circe.Json
import io.circe.parser._
import io.circe.syntax._
import org.scalatest.{Inside, MustMatchers, WordSpec}

class EncryptedSpec extends WordSpec with MustMatchers with Inside {
  implicit val crypto: Crypto = Crypto(AES, secret = "victorias secret".getBytes)

  "Encrypted" should {
    "be converted to bytes and back again" in {
      val bytes = "Hello world".getBytes

      val encryptedBytes = Encrypted(bytes).toBytes
      val encryptedFromBytes: Encrypted[Array[Byte]] = Encrypted.fromBytes[Array[Byte]](encryptedBytes)

      new String(encryptedFromBytes.decrypt) mustEqual "Hello world"
    }

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

    "encrypt and decrypt with explicit Crypto" in {
      val crypto = Crypto(AES, secret = "victorias secret".getBytes)
      val message = "Hello world"

      val encryptedMessage = Encrypted(message, crypto)

      encryptedMessage.decrypt(crypto) mustEqual message
    }

    "encrypt data, serialize, deserialize and then decrypt" in {
      val message = "Hello world"

      val encryptedMessage = Encrypted(message)

      val encryptedBytes: Array[Byte] = ToBytes[Encrypted[String]].apply(encryptedMessage)
      val encryptedMessageResult: Encrypted[String] = FromBytes[Encrypted[String]].apply(encryptedBytes)

      encryptedMessageResult.decrypt mustEqual message
    }

    "encrypt an array of bytes, serialize, deserialize and then decrypt" in {
      val bytes = "Hello world".getBytes

      val encryptedBytes = Encrypted(bytes)
      val encryptedBytesResult = FromBytes[Encrypted[Array[Byte]]].apply(ToBytes[Encrypted[Array[Byte]]].apply(encryptedBytes))

      new String(encryptedBytesResult.decrypt) mustEqual "Hello world"
    }
  }
}