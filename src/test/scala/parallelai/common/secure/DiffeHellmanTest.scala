package parallelai.common.secure

import org.scalatest._

class DiffeHellmanTest extends FlatSpec with Matchers with DiffeHellmanClient with DiffeHellmanServer {
  "Diffe-Hellman Client and Server shared secret" should "be the same" in {
    // Begin Key Exchange
    val clientPubKeyEnc = getClientPublicKey()
    val serverPubKeyEnc = getServerPublicKey(clientPubKeyEnc)
    createClientSharedSecret(serverPubKeyEnc)

    val cs = getClientSharedSecret()
    val ss = getClientSharedSecret()

    cs shouldEqual ss
  }
}
