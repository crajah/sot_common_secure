package parallelai.common.secure

import org.scalatest._

class CryptoTest extends FlatSpec with Matchers with Crypto {
  "AES" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(AES, "Some secrrem is not a secret but secretly some".getBytes()) {}

    val clear = "This is a new logic".getBytes()
    val ec_r = crypto.encrypt(clear)
    val de_r = crypto.decrypt(ec_r.payload, ec_r.params)

    clear shouldEqual de_r.payload
  }

  "DES" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(DES, "Some secrrem is not a secret but secretly some".getBytes()) {}

    val clear = "This is a new logic".getBytes()

    val ec_r = crypto.encrypt(clear)
    val de_r = crypto.decrypt(ec_r.payload, ec_r.params).payload

    clear shouldEqual de_r
  }

  "AES Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(AES, "Some secrrem is not a secret but secretly some".getBytes()) {}

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("AES Sig: " + toHexString(s_p))

    in should not equal s_p

  }

  "DES Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(DES, "Some secrrem is not a secret but secretly some".getBytes()) {}

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("DES Sig: " + toHexString(s_p))

    in should not equal s_p

  }

  "HS512 Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(HS512, "Some secrrem is not a secret but secretly some".getBytes()) {}

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("HS512 Sig: " + toHexString(s_p))

    in should not equal s_p

  }

  "HS384 Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(HS384, "Some secrrem is not a secret but secretly some".getBytes()) {}

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("HS384 Sig: " + toHexString(s_p))

    in should not equal s_p

  }

  "HS256 Signature" should "encrypt and decrypt" in {
    val crypto = new CryptoMechanic(HS256, "Some secrrem is not a secret but secretly some".getBytes()) {}

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("HS256 Sig: " + toHexString(s_p))

    in should not equal s_p

  }

  "B64 Bytes" should "encode and decode" in {
    val ch = new Crypto {}

    val in = "Thi sis a sone ggah".getBytes()

    val b64 = ch.toB64(in)
    val out = ch.fromB64(b64)

    in shouldEqual out
  }

  "B64 String" should "encode and decode" in {
    val ch = new Crypto {}

    val in = "Thi sis a sone ggah".getBytes()

    val b64 = ch.toB64String(in)
    val out = ch.fromB64String(b64)

    in shouldEqual out
  }
}