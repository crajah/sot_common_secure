package parallelai.common.secure

import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import grizzled.slf4j.Logging

trait DiffeHellmanClient extends Logging {
  info("CLIENT: Generate DH keypair ...")
  private val clientKpairGen = KeyPairGenerator.getInstance("DH")
  clientKpairGen.initialize(2048)

  private val clientKpair = clientKpairGen.generateKeyPair

  info("CLIENT: Creates and initializes DH KeyAgreement ...")
  private val clientKeyAgree = KeyAgreement.getInstance("DH")
  clientKeyAgree.init(clientKpair.getPrivate)

  lazy val clientPublicKey: Array[Byte] =
    clientKpair.getPublic.getEncoded

  def clientSharedSecret(serverPubKeyEnc: Array[Byte]): Array[Byte] = {
    val clientKeyFac = KeyFactory.getInstance("DH")
    val x509KeySpec = new X509EncodedKeySpec(serverPubKeyEnc)
    val publicKey = clientKeyFac.generatePublic(x509KeySpec)

    info("CLIENT: Execute PHASE1 ...")
    clientKeyAgree.doPhase(publicKey, true)

    clientKeyAgree.generateSecret
  }
}