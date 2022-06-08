package parallelai.common.secure.diffiehellman

import org.scalatest._
import spray.json._

class PublicKeySpec extends WordSpec with MustMatchers {
  "Client public key" should {
    "be converted to JSON" in {
      val x = DiffieHellmanClient.createClientPublicKey

      println(x.toJson)
    }
  }
}