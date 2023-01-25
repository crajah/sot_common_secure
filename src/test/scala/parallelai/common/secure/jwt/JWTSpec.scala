package parallelai.common.secure.jwt

import org.scalatest.{MustMatchers, WordSpec}
import parallelai.common.secure.jwt.model._
import org.scalatest._
import parallelai.common.secure._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class JWTSpec extends WordSpec with MustMatchers {
  "JWT Transcoder" should {
    "AES Encrypt and Decrypt the Token" in {
      val algo = AES

      implicit val cryptoValue = new JWTCryptoValues {
        override val secret: Array[Byte] = ("Hello World" + " " * 256).getBytes
      }

      implicit val reservedClaims = new JWTReservedClaimValues {
        override val iss: String = "JWT Test"
        override val aud: String = iss
        override val prn: String = iss
        override val jti: String = iss
        override val typ: String = iss
      }

      implicit val headerValues = new JWTHeaderValues {
        override val alg: String = algo.name
      }

      val jwt = JWT.getDefault()
      val token = jwt.getSignature

      val newJWT = JWT.decipher(token).get

      jwt mustEqual newJWT
    }
    "AES Encode and Decode" in {
      val algo = AES

      implicit val cryptoValue = new JWTCryptoValues {
        override val secret: Array[Byte] = ("Hello World" + " " * 256).getBytes
      }

      implicit val reservedClaims = new JWTReservedClaimValues {
        override val iss: String = "JWT Test"
        override val aud: String = iss
        override val prn: String = iss
        override val jti: String = iss
        override val typ: String = iss
      }

      implicit val headerValues = new JWTHeaderValues {
        override val alg: String = algo.name
      }

      val jwt = JWT.getDefault()
      val token = jwt.getPrefixedToken

      val newJWT = JWT.validate(token)
      val newToken = newJWT.get.getPrefixedToken

      jwt mustEqual newJWT.get
      token mustEqual newToken
    }
    "DES Encrypt and Decrypt the Token" in {
      val algo = DES

      implicit val cryptoValue = new JWTCryptoValues {
        override val secret: Array[Byte] = ("Hello World" + " " * 256).getBytes
      }

      implicit val reservedClaims = new JWTReservedClaimValues {
        override val iss: String = "JWT Test"
        override val aud: String = iss
        override val prn: String = iss
        override val jti: String = iss
        override val typ: String = iss
      }

      implicit val headerValues = new JWTHeaderValues {
        override val alg: String = algo.name
      }

      val jwt = JWT.getDefault()
      val token = jwt.getSignature

      val newJWT = JWT.decipher(token).get

      jwt mustEqual newJWT
    }
    "DES Encode and Decode" in {
      val algo = DES

      implicit val cryptoValue = new JWTCryptoValues {
        override val secret: Array[Byte] = ("Hello World" + " " * 256).getBytes
      }

      implicit val reservedClaims = new JWTReservedClaimValues {
        override val iss: String = "JWT Test"
        override val aud: String = iss
        override val prn: String = iss
        override val jti: String = iss
        override val typ: String = iss
      }

      implicit val headerValues = new JWTHeaderValues {
        override val alg: String = algo.name
      }

      val jwt = JWT.getDefault()
      val token = jwt.getPrefixedToken

      val newJWT = JWT.validate(token)
      val newToken = newJWT.get.getPrefixedToken

      jwt mustEqual newJWT.get
      token mustEqual newToken
    }
    "HS256 Encode and Decode" in {
      val algo = HS256

      implicit val cryptoValue = new JWTCryptoValues {
        override val secret: Array[Byte] = ("Hello World" + " " * 256).getBytes
      }

      implicit val reservedClaims = new JWTReservedClaimValues {
        override val iss: String = "JWT Test"
        override val aud: String = iss
        override val prn: String = iss
        override val jti: String = iss
        override val typ: String = iss
      }

      implicit val headerValues = new JWTHeaderValues {
        override val alg: String = algo.name
      }

      val jwt = JWT.getDefault()
      val token = jwt.getPrefixedToken

      val newJWT = JWT.validate(token)
      val newToken = newJWT.get.getPrefixedToken

      jwt mustEqual newJWT.get
      token mustEqual newToken
    }
    "HS384 Encode and Decode" in {
      val algo = HS384

      implicit val cryptoValue = new JWTCryptoValues {
        override val secret: Array[Byte] = ("Hello World" + " " * 256).getBytes
      }

      implicit val reservedClaims = new JWTReservedClaimValues {
        override val iss: String = "JWT Test"
        override val aud: String = iss
        override val prn: String = iss
        override val jti: String = iss
        override val typ: String = iss
      }

      implicit val headerValues = new JWTHeaderValues {
        override val alg: String = algo.name
      }

      val jwt = JWT.getDefault()
      val token = jwt.getPrefixedToken

      val newJWT = JWT.validate(token)
      val newToken = newJWT.get.getPrefixedToken

      jwt mustEqual newJWT.get
      token mustEqual newToken
    }
    "HS512 Encode and Decode" in {
      val algo = HS512

      implicit val cryptoValue = new JWTCryptoValues {
        override val secret: Array[Byte] = ("Hello World" + " " * 256).getBytes
      }

      implicit val reservedClaims = new JWTReservedClaimValues {
        override val iss: String = "JWT Test"
        override val aud: String = iss
        override val prn: String = iss
        override val jti: String = iss
        override val typ: String = iss
      }

      implicit val headerValues = new JWTHeaderValues {
        override val alg: String = algo.name
      }

      val jwt = JWT.getDefault()
      val token = jwt.getPrefixedToken

      val newJWT = JWT.validate(token)
      val newToken = newJWT.get.getPrefixedToken

      jwt mustEqual newJWT.get
      token mustEqual newToken
    }
  }
}
