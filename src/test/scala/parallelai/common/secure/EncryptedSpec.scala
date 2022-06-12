package parallelai.common.secure

import io.circe.syntax._
import spray.json._
import org.scalatest.{Inside, MustMatchers, WordSpec}

class EncryptedSpec extends WordSpec with MustMatchers with Inside {
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

  "Encrypteds" should {
    "have each entry encrypted and decrypted as a Map of Encrypted" in {
      val message1 = "Hello world"
      val message2 = 99

      val encryptedMessages = Encrypteds("message1" -> Encrypted(message1), "message2" -> Encrypted(message2))
      encryptedMessages("message1").decryptT[String] mustEqual message1

      inside(encryptedMessages.get("message2")) {
        case Some(encrypted) => encrypted.decryptT[Int] mustEqual message2
      }
    }

    "be converted to and from Spray JSON" in {
      val message1 = "Hello world"
      val message2 = 99

      val encrypteds = Encrypteds("message1" -> Encrypted(message1), "message2" -> Encrypted(message2))
      val encryptedsJson = encrypteds.toJson

      encryptedsJson.asJsObject.fields must (contain key "message1" and contain key "message2")
      encryptedsJson.convertTo[Encrypteds].values must (contain key "message1" and contain key "message2")
    }

    "be converted to and from Circe JSON" in {
      val message1 = "Hello world"
      val message2 = 99

      val encrypteds = Encrypteds("message1" -> Encrypted(message1), "message2" -> Encrypted(message2))
      val encryptedsJson = encrypteds.asJson

      encryptedsJson.as[Encrypteds].right.get.values must (contain key "message1" and contain key "message2")
    }

    "be converted to and from bytes" in {
      def toBytes[T: ToBytes](value: T): Array[Byte] = ToBytes[T].apply(value)

      val message1 = "Hello world"
      val message2 = 99

      val encrypteds = Encrypteds("message1" -> Encrypted(message1), "message2" -> Encrypted(message2))
      val serialized = toBytes(encrypteds)
      val encryptedsDeserialized = FromBytes[Encrypteds].apply(serialized)

      encryptedsDeserialized.values must (contain key "message1" and contain key "message2")
    }
  }
}