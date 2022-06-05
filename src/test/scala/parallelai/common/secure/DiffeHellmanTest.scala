package parallelai.common.secure

import org.scalatest._

class DiffeHellmanTest extends FlatSpec with Matchers with DiffeHellmanClient with DiffeHellmanServer {
  "Diffe-Hellman Client and Server shared secret" should "be the same" in {
    // Begin Key Exchange
    val clientKey = clientPublicKey
    val serverKey = getServerPublicKey(clientKey)
    val clientSecret = clientSharedSecret(serverKey)

    // TODO
    /*val cs = getClientSharedSecret
    val ss = getClientSharedSecret

    cs shouldEqual ss*/
  }
}