package parallelai.common.secure

import org.scalatest._

class CryptoSpec extends WordSpec with MustMatchers with Crypto {
  "B64 Bytes" should {
    "encode and decode" in {
      val in = "Thi sis a sone ggah".getBytes()

      val b64 = toB64(in)
      val out = fromB64(b64)

      in mustEqual out
    }
  }

  "B64 String" should {
    "encode and decode" in {
      val in = "Thi sis a sone ggah".getBytes()

      val b64 = toB64String(in)
      val out = fromB64String(b64)

      in mustEqual out
    }
  }
}