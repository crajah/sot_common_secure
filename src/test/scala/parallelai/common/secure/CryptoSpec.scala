package parallelai.common.secure

import org.scalatest._

class CryptoSpec extends FlatSpec with Matchers {
  "AES" should "encrypt and decrypt" in {
    val crypto = new Crypto(AES, "Some secrrem is not a secret but secretly some".getBytes())

    val clear = "This is a new logic".getBytes()
    val ec_r = crypto.encrypt(clear)
    val de_r = crypto.decrypt(ec_r.payload, ec_r.params)

    clear shouldEqual de_r.payload
  }

  "DES" should "encrypt and decrypt" in {
    val crypto = new Crypto(DES, "Some secrrem is not a secret but secretly some".getBytes())

    val clear = "This is a new logic".getBytes()

    val ec_r = crypto.encrypt(clear)
    val de_r = crypto.decrypt(ec_r.payload, ec_r.params).payload

    clear shouldEqual de_r
  }

  "AES Signature" should "encrypt and decrypt" in {
    val crypto = new Crypto(AES, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("AES Sig: " + Crypto.toHexString(s_p))

    in should not equal s_p
  }

  "DES Signature" should "encrypt and decrypt" in {
    val crypto = new Crypto(DES, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("DES Sig: " + Crypto.toHexString(s_p))

    in should not equal s_p
  }

  "HS512 Signature" should "encrypt and decrypt" in {
    val crypto = new Crypto(HS512, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("HS512 Sig: " + Crypto.toHexString(s_p))

    in should not equal s_p
  }

  "HS384 Signature" should "encrypt and decrypt" in {
    val crypto = new Crypto(HS384, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("HS384 Sig: " + Crypto.toHexString(s_p))

    in should not equal s_p
  }

  "HS256 Signature" should "encrypt and decrypt" in {
    val crypto = new Crypto(HS256, "Some secrrem is not a secret but secretly some".getBytes())

    val in = "Some crazy logic".getBytes()

    val s_p = crypto.getSignature(in)

    println("HS256 Sig: " + crypto.toHexString(s_p))

    in should not equal s_p
  }

  "B64 Bytes" should "encode and decode" in {
    val in = "Thi sis a sone ggah".getBytes()

    val b64 = Crypto.toB64(in)
    val out = Crypto.fromB64(b64)

    in shouldEqual out
  }

  "B64 String" should "encode and decode" in {
    val in = "Thi sis a sone ggah".getBytes()

    val b64 = Crypto.toB64String(in)
    val out = Crypto.fromB64String(b64)

    in shouldEqual out
  }
}