package parallelai.common.secure.diffiehellman

import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import grizzled.slf4j.Logging

trait DiffieHellmanClient extends Logging {
  info("Client: Generate DH keypair ...")
  private val clientKeyPairGenerator = KeyPairGenerator.getInstance("DH")
  clientKeyPairGenerator.initialize(2048)

  private val clientKeyPair = clientKeyPairGenerator.generateKeyPair

  // Client creates and initializes her DH KeyAgreement object
  info("Client: Initialization ...")
  private val clientKeyAgreement = KeyAgreement.getInstance("DH")
  clientKeyAgreement.init(clientKeyPair.getPrivate)

  lazy val clientPublicKey: ClientPublicKey =
    new ClientPublicKey(clientKeyPair.getPublic.getEncoded)

  //val clientSharedSecret:
  def clientSharedSecret(serverKey: ServerKey): ClientSharedSecret = {
    val clientKeyFactory = KeyFactory.getInstance("DH")
    val x509KeySpec = new X509EncodedKeySpec(serverKey.publicKey)

    info("Client: Execute PHASE1 ...")
    clientKeyAgreement.doPhase(clientKeyFactory.generatePublic(x509KeySpec), true)

    // Client shared secret
    new ClientSharedSecret(clientKeyAgreement.generateSecret)
  }
}