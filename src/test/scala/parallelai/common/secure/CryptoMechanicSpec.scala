package parallelai.common.secure

import org.scalatest._

class CryptoMechanicSpec extends FlatSpec with Matchers with Crypto {
  "AES" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(AES, "Some secrrem is not a secret but secretly some".getBytes())

    val clear = "This is a new logic".getBytes()
    val ec_r = crypto.encrypt(clear)
    val de_r = crypto.decrypt(ec_r.payload, ec_r.params)

    clear shouldEqual de_r.payload
  }

  "DES" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(DES, "Some secrrem is not a secret but secretly some".getBytes())

    val clear = "This is a new logic".getBytes()

    val ec_r = crypto.encrypt(clear)
    val de_r = crypto.decrypt(ec_r.payload, ec_r.params).payload

    clear shouldEqual de_r
  }

  "AES Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(AES, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("AES Sig: " + toHexString(s_p))

    in should not equal s_p

  }

  "DES Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(DES, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("DES Sig: " + toHexString(s_p))

    in should not equal s_p

  }

  "HS512 Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(HS512, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("HS512 Sig: " + toHexString(s_p))

    in should not equal s_p

  }

  "HS384 Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(HS384, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("HS384 Sig: " + toHexString(s_p))

    in should not equal s_p

  }

  "HS256 Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(HS256, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("HS256 Sig: " + toHexString(s_p))

    in should not equal s_p
  }
}