package parallelai.common.secure.diffiehellman

import java.security.KeyPair

case class ServerPublicKey(value: Array[Byte], keyPair: KeyPair)