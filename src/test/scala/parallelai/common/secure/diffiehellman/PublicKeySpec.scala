package parallelai.common.secure.diffiehellman

import org.scalatest._
import spray.json._
import io.circe.syntax._
import parallelai.common.secure.{FromBytes, ToBytes}

class PublicKeySpec extends WordSpec with MustMatchers {
  val clientPublicKey: ClientPublicKey = DiffieHellmanClient.createClientPublicKey

  "Client public key" should {
    "be converted to Spray JSON" in {
      clientPublicKey.toJson.convertTo[ClientPublicKey].value mustEqual clientPublicKey.value
    }

    "be converted to Circe JSON" in {
      clientPublicKey.asJson.as[ClientPublicKey].right.get.value mustEqual clientPublicKey.value
    }

    "be serialized and deserialized" in {
      val serialized = ToBytes[ClientPublicKey].apply(clientPublicKey)
      val deserialized = FromBytes[ClientPublicKey].apply(serialized)

      deserialized.value mustEqual clientPublicKey.value
    }
  }
}