package parallelai.common.secure

import org.scalatest._

class AesMechanicTest extends FlatSpec with Matchers with DiffeHellmanClient with DiffeHellmanServer {
  "AES Mechanic" should "encode and decode correctly" in {

    val clientPubKeyEnc = getClientPublicKey()
    val serverPubKeyEnc = getServerPublicKey(clientPubKeyEnc)
    createClientSharedSecret(serverPubKeyEnc)

    val clear = "This is quite cool".getBytes()

    val serverAES = new AesMechanic(getServerSharedSecret().get)

    serverAES.encrypt(clear).flatMap { ae =>
      val clientAES = new AesMechanic(getClientSharedSecret().get, Some(ae.params))
      clientAES.decrypt(ae.encoded)
    }.map { ne =>
      clear shouldEqual ne
    }
  }
}
