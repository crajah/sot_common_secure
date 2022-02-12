package parallelai.common.secure

import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement

trait DiffeHellmanClient {

  println("CLIENT: Generate DH keypair ...")
  private val clientKpairGen = KeyPairGenerator.getInstance("DH")
  clientKpairGen.initialize(2048)
  private val clientKpair = clientKpairGen.generateKeyPair

  // Client creates and initializes her DH KeyAgreement object
  println("CLIENT: Initialization ...")
  private val clientKeyAgree = KeyAgreement.getInstance("DH")
  clientKeyAgree.init(clientKpair.getPrivate)

  private var clientSharedSecret: Option[Array[Byte]] = None

  def getClientPublicKey(): Array[Byte] = {
    // Client encodes her public key, and sends it over to Bob.
    val clientPubKeyEnc = clientKpair.getPublic.getEncoded

    clientPubKeyEnc
  }

  def createClientSharedSecret(serverPubKeyEnc: Array[Byte]) = {
    val clientKeyFac = KeyFactory.getInstance("DH")
    val x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc)
    val bobPubKey = clientKeyFac.generatePublic(x509KeySpec)
    println("CLIENT: Execute PHASE1 ...")
    clientKeyAgree.doPhase(bobPubKey, true)

    clientSharedSecret = Some(clientKeyAgree.generateSecret)
  }

  def getClientSharedSecret() = clientSharedSecret
}
