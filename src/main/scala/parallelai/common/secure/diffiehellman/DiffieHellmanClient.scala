package parallelai.common.secure.diffiehellman

import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import grizzled.slf4j.Logging

object DiffieHellmanClient extends Logging {
  info("Client: Generate DH keypair ...")
  val clientKeyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("DH")
  clientKeyPairGenerator.initialize(2048)

  val clientKeyPair: KeyPair = clientKeyPairGenerator.generateKeyPair

  info("Client: Initialization - create and initialize DH KeyAgreement object ...")
  val clientKeyAgreement: KeyAgreement = KeyAgreement.getInstance("DH")
  clientKeyAgreement.init(clientKeyPair.getPrivate)

  val clientPublicKey: ClientPublicKey =
    new ClientPublicKey(clientKeyPair.getPublic.getEncoded)

  def create(serverKey: ServerPublicKey): ClientSharedSecret = {
    val clientKeyFactory = KeyFactory.getInstance("DH")
    val x509KeySpec = new X509EncodedKeySpec(serverKey.value)

    info("Client: Execute PHASE1 ...")
    clientKeyAgreement.doPhase(clientKeyFactory.generatePublic(x509KeySpec), true)

    new ClientSharedSecret(clientKeyAgreement.generateSecret)
  }
}