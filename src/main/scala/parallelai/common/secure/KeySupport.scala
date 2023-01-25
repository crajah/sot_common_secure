package parallelai.common.secure

import java.io.InputStream
import java.security.{KeyStore, MessageDigest, PrivateKey}
import javax.net.ssl.{KeyManagerFactory, TrustManagerFactory}

trait KeySupport {
  val keyStoreName: String
  val keyStoreType: String
  val keyStorePass: String
  val keyAlias: String

  lazy val defaultSecret: Array[Byte] = getSecret(defaultKeyStore, keyAlias, keyStorePass)

  lazy val defaultKeyStore: KeyStore = getKeyStore(keyStoreName, keyStoreType, keyStorePass)

  def getKeyStore(keyStoreName: String, keyStoreType: String, keyStorePassword: String): KeyStore = {
    val password: Array[Char] = keyStorePassword.toCharArray

    val ks: KeyStore = KeyStore.getInstance(keyStoreType)
    val keystore: InputStream = getClass.getClassLoader.getResourceAsStream(keyStoreName)

    require(keystore != null, "Keystore required!")
    ks.load(keystore, password)

    ks
  }

  def getSecret(ks: KeyStore, alias: String, password: String): Array[Byte] = {
    val key = ks.getKey(alias, password.toCharArray).asInstanceOf[PrivateKey]
    MessageDigest.getInstance("SHA-256").digest(key.getEncoded)
    //.map("%02X" format _).mkString
  }

  def getDefaultKeyManagerFactory: KeyManagerFactory = {
    val password: Array[Char] = keyStorePass.toCharArray
    val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
    keyManagerFactory.init(defaultKeyStore, password)

    keyManagerFactory
  }

  def getDefaultTrustManagerFactory: TrustManagerFactory = {
    val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
    tmf.init(defaultKeyStore)

    tmf
  }
}

object DefaultDESCrypto extends KeySupport {
  val keyStoreName: String = "testKeyStore"
  val keyStoreType: String = "jks"
  val keyStorePass: String = "password"
  val keyAlias: String = "test"

  val crypto = new Crypto(DES, defaultSecret)
}