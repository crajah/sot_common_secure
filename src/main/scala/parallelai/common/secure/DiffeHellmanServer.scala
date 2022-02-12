package parallelai.common.secure

import java.security.spec.X509EncodedKeySpec
import java.security.{ KeyFactory, KeyPairGenerator }
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHPublicKey

trait DiffeHellmanServer {
  private var serverSharedSecret: Option[Array[Byte]] = None

  def getServerPublicKey(clientPubKeyEnc: Array[Byte]): Array[Byte] = {
    val serverKeyFac = KeyFactory.getInstance("DH")
    var x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc)

    val clientPubKey = serverKeyFac.generatePublic(x509KeySpec)

    /*
     * Server gets the DH parameters associated with Alice's public key.
     * He must use the same parameters when he generates his own key
     * pair.
     */
    val dhParamFromClientPubKey = clientPubKey.asInstanceOf[DHPublicKey].getParams

    // Server creates his own DH key pair
    println("SERVER: Generate DH keypair ...")
    val serverKpairGen = KeyPairGenerator.getInstance("DH")
    serverKpairGen.initialize(dhParamFromClientPubKey)
    val serverKpair = serverKpairGen.generateKeyPair

    // Server creates and initializes his DH KeyAgreement object
    println("SERVER: Initialization ...")
    val serverKeyAgree = KeyAgreement.getInstance("DH")
    serverKeyAgree.init(serverKpair.getPrivate)

    // Server encodes his public key, and sends it over to Alice.
    val serverPubKeyEnc = serverKpair.getPublic.getEncoded

    println("SERVER: Execute PHASE1 ...")
    serverKeyAgree.doPhase(clientPubKey, true)

    serverSharedSecret = Some(serverKeyAgree.generateSecret())

    serverPubKeyEnc
  }

  def getServerSharedSecret() = serverSharedSecret
}
