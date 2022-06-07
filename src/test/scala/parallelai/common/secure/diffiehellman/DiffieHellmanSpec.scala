package parallelai.common.secure.diffiehellman

import org.scalatest._

class DiffieHellmanSpec extends WordSpec with MustMatchers with DiffieHellmanClient with DiffieHellmanServer {
  "Diffie-Hellman Client and Server shared secret" should {
    "be the same" in {
      // Begin Key Exchange
      val sk: ServerKey = serverKey(clientPublicKey)

      val css: ClientSharedSecret = clientSharedSecret(sk)

      css.value mustEqual sk.sharedSecret
    }
  }
}