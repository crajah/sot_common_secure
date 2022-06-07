package parallelai.common.secure.diffiehellman

import java.security._
import java.security.spec.X509EncodedKeySpec
import javax.crypto.KeyAgreement
import grizzled.slf4j.Logging

object DiffieHellmanClient extends Logging {
  def createClientPublicKey: ClientPublicKey = {
    info("DH Client: Generate DH keypair ...")
    val clientKeyPairGenerator = KeyPairGenerator getInstance "DH"
    clientKeyPairGenerator initialize 2048

    val clientKeyPair = clientKeyPairGenerator.generateKeyPair

    ClientPublicKey(clientKeyPair.getPublic.getEncoded, clientKeyPair)
  }

  def createClientSharedSecret(serverPublicKey: ServerPublicKey): ClientSharedSecret = {
    val clientKeyFactory = KeyFactory getInstance "DH"
    val x509KeySpec = new X509EncodedKeySpec(serverPublicKey.value)

    info("DH Client: Initialization - create and initialize DH KeyAgreement object ...")
    val clientKeyAgreement: KeyAgreement = KeyAgreement getInstance "DH"
    clientKeyAgreement init serverPublicKey.keyPair.getPrivate

    info("DH Client: Execute PHASE1 ...")
    clientKeyAgreement.doPhase(clientKeyFactory.generatePublic(x509KeySpec), true)

    ClientSharedSecret(clientKeyAgreement.generateSecret)
  }
}