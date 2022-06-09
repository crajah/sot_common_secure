package parallelai.common.secure.diffiehellman

import org.scalatest._
import spray.json._

class PublicKeySpec extends WordSpec with MustMatchers {
  "Client public key" should {
    "be converted to JSON" in {
      val clientPublicKey = DiffieHellmanClient.createClientPublicKey

      clientPublicKey.toJson.convertTo[ClientPublicKey].value mustEqual clientPublicKey.value
    }
  }
}