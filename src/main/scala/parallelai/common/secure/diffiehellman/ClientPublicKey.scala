package parallelai.common.secure.diffiehellman

import java.security.{ KeyPair, KeyPairGenerator }
import grizzled.slf4j.Logging

case class ClientPublicKey(value: Array[Byte], keyPair: KeyPair)

object ClientPublicKey extends Logging {
  def apply(): ClientPublicKey = {
    info("Client: Generate DH keypair ...")
    val clientKeyPairGenerator = KeyPairGenerator getInstance "DH"
    clientKeyPairGenerator initialize 2048

    val clientKeyPair = clientKeyPairGenerator.generateKeyPair

    ClientPublicKey(clientKeyPair.getPublic.getEncoded, clientKeyPair)
  }
}