package parallelai.common.secure.diffiehellman

import java.security.spec.X509EncodedKeySpec
import java.security.{ KeyFactory, KeyPairGenerator }
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHPublicKey
import grizzled.slf4j.Logging

trait DiffieHellmanServer extends Logging {
  def serverPublicKey(clientPublicKey: Array[Byte]): (Array[Byte], Array[Byte]) = {
    val serverKeyFac = KeyFactory.getInstance("DH")

    val x509KeySpec = new X509EncodedKeySpec(clientPublicKey)

    val publicKey = serverKeyFac.generatePublic(x509KeySpec)

    val dhParamFromClientPubKey = publicKey.asInstanceOf[DHPublicKey].getParams

    info("SERVER: Generate DH keypair ...")
    val serverKpairGen = KeyPairGenerator.getInstance("DH")
    serverKpairGen.initialize(dhParamFromClientPubKey)

    val serverKpair = serverKpairGen.generateKeyPair

    info("SERVER: Creates and initializes his DH KeyAgreement ...")
    val serverKeyAgree = KeyAgreement.getInstance("DH")
    serverKeyAgree.init(serverKpair.getPrivate)

    val serverPublicKey = serverKpair.getPublic.getEncoded

    info("SERVER: Execute PHASE1 ...")
    serverKeyAgree.doPhase(publicKey, true)

    val sharedSecret = serverKeyAgree.generateSecret()

    (serverPublicKey, sharedSecret)
  }
}