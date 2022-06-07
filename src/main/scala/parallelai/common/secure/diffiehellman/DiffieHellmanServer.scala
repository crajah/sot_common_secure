package parallelai.common.secure.diffiehellman

import java.security.spec.X509EncodedKeySpec
import java.security.{ KeyFactory, KeyPairGenerator }
import javax.crypto.KeyAgreement
import javax.crypto.interfaces.DHPublicKey
import javax.crypto.spec.DHParameterSpec
import grizzled.slf4j.Logging

object DiffieHellmanServer extends Logging {
  def create(clientPublicKey: ClientPublicKey): (ServerPublicKey, ServerSharedSecret) = {
    val serverKeyFactory = KeyFactory.getInstance("DH")
    val x509KeySpec = new X509EncodedKeySpec(clientPublicKey.value)

    val publicKey = serverKeyFactory.generatePublic(x509KeySpec)

    val dHParameterSpec: DHParameterSpec = publicKey.asInstanceOf[DHPublicKey].getParams

    info("Server: Generate DH keypair ...")
    val serverKeyPairGenerator = KeyPairGenerator.getInstance("DH")
    serverKeyPairGenerator.initialize(dHParameterSpec)
    val serverKeyPair = serverKeyPairGenerator.generateKeyPair

    info("Server: Initialization - create and initialize DH KeyAgreement object ...")
    val serverKeyAgreement = KeyAgreement.getInstance("DH")
    serverKeyAgreement.init(serverKeyPair.getPrivate)

    info("Server: Execute PHASE1 ...")
    serverKeyAgreement.doPhase(publicKey, true)

    (new ServerPublicKey(serverKeyPair.getPublic.getEncoded), new ServerSharedSecret(serverKeyAgreement.generateSecret()))
  }
}