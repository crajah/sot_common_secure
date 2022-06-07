package parallelai.common.secure.diffiehellman

import org.scalatest._

class DiffieHellmanSpec extends WordSpec with MustMatchers with DiffieHellmanClient with DiffieHellmanServer {
  "Diffie-Hellman Client and Server shared secret" should {
    "be the same" in {
      // Begin Key Exchange
      val clientKey = clientPublicKey
      val (serverKey, shared) = serverPublicKey(clientKey)
      val clientSecret = clientSharedSecret(serverKey)

      println(clientKey)
      println(serverKey)
      println(shared)
      println(clientSecret)

      // TODO
      /*val cs = getClientSharedSecret
      val ss = getClientSharedSecret

      cs shouldEqual ss*/
    }
  }
}