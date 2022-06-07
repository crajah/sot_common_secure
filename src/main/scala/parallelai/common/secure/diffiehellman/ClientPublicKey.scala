package parallelai.common.secure.diffiehellman

import java.security.KeyPair

case class ClientPublicKey(value: Array[Byte], keyPair: KeyPair)