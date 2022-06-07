package parallelai.common.secure.diffiehellman

import org.scalatest._

class DiffieHellmanSpec extends WordSpec with MustMatchers {
  "Diffie-Hellman Client and Server shared secret" should {
    "be the same" in {
      val (serverPublicKey, serverSharedSecret) = DiffieHellmanServer.create(ClientPublicKey())

      val clientSharedSecret: ClientSharedSecret = DiffieHellmanClient.create(serverPublicKey)

      clientSharedSecret.value mustEqual serverSharedSecret.value
    }
  }
}