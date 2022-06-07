package parallelai.common.secure.diffiehellman

import org.scalatest._

class DiffieHellmanExperimentSpec extends WordSpec with MustMatchers {
  "Blah" should {
    "blah" in {
      val alice: Person = new Person
      val bob: Person = new Person

      alice.generateKeys
      bob.generateKeys

      alice.receivePublicKeyFrom(bob)
      bob.receivePublicKeyFrom(alice)

      val a = alice.generateCommonSecretKey
      val b = bob.generateCommonSecretKey

      println(a)
      println(b)

      alice.encryptAndSendMessage("Bob! Guess Who I am.", bob)

      bob.whisperTheSecretMessage()
    }
  }
}

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.SecretKeySpec

class Person { //~ --- [INSTANCE FIELDS] ------------------------------------------------------------------------------------------
  private var privateKey: PrivateKey = _
  private var publicKey: PublicKey = _
  private var receivedPublicKey: PublicKey = _
  private var secretKey: Array[Byte] = _
  private var secretMessage: String = _

  //~ --- [METHODS] --------------------------------------------------------------------------------------------------
  def encryptAndSendMessage(message: String, person: Person): Unit = {
    try { // You can use Blowfish or another symmetric algorithm but you must adjust the key size.
      val keySpec = new SecretKeySpec(secretKey, "DES")
      val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
      cipher.init(Cipher.ENCRYPT_MODE, keySpec)
      val encryptedMessage = cipher.doFinal(message.getBytes)
      person.receiveAndDecryptMessage(encryptedMessage)
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  //~ ----------------------------------------------------------------------------------------------------------------
  def generateCommonSecretKey: Array[Byte] = {
    try {
      val keyAgreement = KeyAgreement.getInstance("DH")
      keyAgreement.init(privateKey)
      keyAgreement.doPhase(receivedPublicKey, true)
      secretKey = shortenSecretKey(keyAgreement.generateSecret)
      secretKey
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw e
    }
  }

  def generateKeys(): Unit = {
    try {
      val keyPairGenerator = KeyPairGenerator.getInstance("DH")
      keyPairGenerator.initialize(1024)
      val keyPair = keyPairGenerator.generateKeyPair
      privateKey = keyPair.getPrivate
      publicKey = keyPair.getPublic
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  def getPublicKey: PublicKey = publicKey

  def receiveAndDecryptMessage(message: Array[Byte]): Unit = {
    try {
      val keySpec = new SecretKeySpec(secretKey, "DES")
      val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
      cipher.init(Cipher.DECRYPT_MODE, keySpec)
      secretMessage = new String(cipher.doFinal(message))
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
  }

  /**
   * In a real life example you must serialize the public key for transferring.
   *
   * @param  person
   */
  def receivePublicKeyFrom(person: Person): Unit = {
    receivedPublicKey = person.getPublicKey
  }

  def whisperTheSecretMessage(): Unit = {
    println(secretMessage)
  }

  /**
   * 1024 bit symmetric key size is so big for DES so we must shorten the key size. You can get first 8 longKey of the
   * byte array or can use a key factory
   *
   * @param   longKey
   * @return
   */
  private def shortenSecretKey(longKey: Array[Byte]): Array[Byte] = {
    try { // Use 8 bytes (64 bits) for DES, 6 bytes (48 bits) for Blowfish
      val shortenedKey = new Array[Byte](8)
      System.arraycopy(longKey, 0, shortenedKey, 0, shortenedKey.length)
      return shortenedKey
      // Below lines can be more secure
      // final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
      // final DESKeySpec       desSpec    = new DESKeySpec(longKey);
      //
      // return keyFactory.generateSecret(desSpec).getEncoded();
    } catch {
      case e: Exception =>
        e.printStackTrace()
    }
    null
  }
}