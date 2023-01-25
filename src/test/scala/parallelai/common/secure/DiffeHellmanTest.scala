package parallelai.common.secure

import org.scalatest._

class DiffeHellmanTest extends FlatSpec with Matchers with DiffeHellmanClient with DiffeHellmanServer with ConversionHelper {
  "Diffe-Hellman Client and Server shared secret" should "be the same" in {
    // Begin Key Exchange
    val clientPubKeyEnc = getClientPublicKey()
    val serverPubKeyEnc = getServerPublicKey(clientPubKeyEnc)
    createClientSharedSecret(serverPubKeyEnc)

    val cs = getClientSharedSecret()
    val ss = getClientSharedSecret()

    println("Shared Secret CS : " + toHexString(cs.get))
    println("Shared Secret SS : " + toHexString(ss.get))

    cs shouldEqual ss

    val crypto = new CryptoMechanic(AES, ss.get) {}

    val clear = "This is a new logic".getBytes()

    val ec_r = crypto.encrypt(clear)

    println("Secret Payload : " + toHexString(ec_r.payload))
    println("Secret Params : " + toHexString(ec_r.params.get))

    val de_r = crypto.decrypt(ec_r.payload, ec_r.params).payload

    clear shouldEqual de_r

  }
}
