package parallelai.common.secure.diffiehellman

import java.security.spec.X509EncodedKeySpec
import java.security.{ KeyFactory, KeyPairGenerator }
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHPublicKey
import javax.crypto.spec.DHParameterSpec
import grizzled.slf4j.Logging

trait DiffieHellmanServer extends Logging {
  def serverKey(clientPublicKey: ClientPublicKey): ServerKey = {
    val serverKeyFactory = KeyFactory.getInstance("DH")
    val x509KeySpec = new X509EncodedKeySpec(clientPublicKey.value)

    val publicKey = serverKeyFactory.generatePublic(x509KeySpec)

    val dHParameterSpec: DHParameterSpec = publicKey.asInstanceOf[DHPublicKey].getParams

    info("Server: Generate DH keypair ...")
    val serverKeyPairGenerator = KeyPairGenerator.getInstance("DH")
    serverKeyPairGenerator.initialize(dHParameterSpec)
    val serverKeyPair = serverKeyPairGenerator.generateKeyPair

    // Server creates and initializes his DH KeyAgreement object
    info("Server: Initialization - create and initialize DH KeyAgreement object ...")
    val serverKeyAgree = KeyAgreement.getInstance("DH")
    serverKeyAgree.init(serverKeyPair.getPrivate)

    info("Server: Execute PHASE1 ...")
    serverKeyAgree.doPhase(publicKey, true)

    ServerKey(serverKeyPair.getPublic.getEncoded, serverKeyAgree.generateSecret())
  }
}