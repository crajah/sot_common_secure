package parallelai.common.secure.diffiehellman

import org.scalatest._

class DiffieHellmanSpec extends WordSpec with MustMatchers {
  "Diffie-Hellman Client and Server shared secret" should {
    "be the same" in {
      val (serverPublicKey, serverSharedSecret) = DiffieHellmanServer.create(DiffieHellmanClient.createClientPublicKey)

      val clientSharedSecret: ClientSharedSecret = DiffieHellmanClient.createClientSharedSecret(serverPublicKey)

      clientSharedSecret.value mustEqual serverSharedSecret.value
    }
  }
}