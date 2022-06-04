package parallelai.common.secure

import java.security.spec.X509EncodedKeySpec
import java.security.{ KeyFactory, KeyPairGenerator }
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHPublicKey
import grizzled.slf4j.Logging

trait DiffeHellmanServer extends Logging {
  private var serverSharedSecret: Option[Array[Byte]] = None

  def getServerPublicKey(clientPubKeyEnc: Array[Byte]): Array[Byte] = {
    val serverKeyFac = KeyFactory.getInstance("DH")

    val x509KeySpec = new X509EncodedKeySpec(clientPubKeyEnc)

    val clientPubKey = serverKeyFac.generatePublic(x509KeySpec)

    val dhParamFromClientPubKey = clientPubKey.asInstanceOf[DHPublicKey].getParams

    info("SERVER: Generate DH keypair ...")
    val serverKpairGen = KeyPairGenerator.getInstance("DH")
    serverKpairGen.initialize(dhParamFromClientPubKey)

    val serverKpair = serverKpairGen.generateKeyPair

    info("SERVER: Creates and initializes his DH KeyAgreement ...")
    val serverKeyAgree = KeyAgreement.getInstance("DH")
    serverKeyAgree.init(serverKpair.getPrivate)

    val serverPubKeyEnc = serverKpair.getPublic.getEncoded

    info("SERVER: Execute PHASE1 ...")
    serverKeyAgree.doPhase(clientPubKey, true)

    serverSharedSecret = Some(serverKeyAgree.generateSecret())

    serverPubKeyEnc
  }

  def getServerSharedSecret: Option[Array[Byte]] = serverSharedSecret
}