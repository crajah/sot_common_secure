package parallelai.common.secure

import javax.crypto.SecretKey
import org.scalatest.{MustMatchers, WordSpec}

class ToAndFromBytesSpec extends WordSpec with MustMatchers {
  "To and from bytes" should {
    "serialize and deserialize a SecretKey" in {
      val secretKey = Crypto.aesSecretKey

      FromBytes[SecretKey].apply(ToBytes[SecretKey].apply(secretKey)) mustEqual secretKey
    }
  }
}