package parallelai.common.secure.jwt.model

import java.util.Base64

import cats.Monad
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import org.joda.time.{DateTime, DateTimeZone}
import parallelai.common.secure.{Algorithm, Crypto, CryptoUtil}
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.language.implicitConversions
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait JWTAlgorithmValue {
  def algorithm(implicit hv: JWTHeaderValues): Algorithm = Algorithm(hv.alg)
}

trait JWTSecretValue {
  val secret: Array[Byte]
}

trait JWTCryptoValues extends JWTAlgorithmValue with JWTSecretValue

case class JWT(header: JWTHeader, reserved: JWTReservedClaims, default: JWTDefaultClaims, payload: Map[String, String]) {

  def getPrefix()(implicit cvals: JWTCryptoValues, hv: JWTHeaderValues) = {
    val crypto = new Crypto(cvals.algorithm, cvals.secret)

    val headerJson = header.copy(alg = crypto.getAlgorithm.name).toJson.toString
    val headerB64String = Base64.getUrlEncoder.encodeToString(headerJson.getBytes(crypto.charset))

    val reservedClaimsJson = reserved.toJson
    val defaulClaimsJson = default.toJson
    val payloadMap = payload

    val allClaims = reservedClaimsJson.asJsObject.fields ++ defaulClaimsJson.asJsObject.fields ++ payloadMap.map(p => { (p._1, JsString(p._2)) })
    val claimsB64String = Base64.getUrlEncoder.encodeToString(JsObject(allClaims).toString.getBytes(crypto.charset))

    headerB64String + "." + claimsB64String
  }

  def getSignature()(implicit cvals: JWTCryptoValues, hv: JWTHeaderValues): String = {
    val crypto = new Crypto(cvals.algorithm, cvals.secret)

    val prefix = getPrefix
    val signature = crypto.getB64Signature(prefix)

    signature
  }

  def getPrefixedToken()(implicit cvals: JWTCryptoValues, hv: JWTHeaderValues) = {
    getPrefix + "." + getSignature
  }
}

object JWT extends CryptoUtil {
  implicit val rootJsonFormat: RootJsonFormat[JWT] = jsonFormat4(JWT.apply)

  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {
    val formatter: DateTimeFormatter = ISODateTimeFormat.basicDateTime

    def write(obj: DateTime): JsValue =
      JsString(formatter.print(obj.withZone(DateTimeZone.UTC)))

    def read(json: JsValue): DateTime = json match {
      case JsString(s) => try {
        formatter.parseDateTime(s).withZone(DateTimeZone.UTC)
      } catch {
        case _: Throwable => error(s)
      }
      case _ =>
        error(json.toString())
    }

    def error(v: Any): DateTime = {
      val example = formatter.print(0)
      deserializationError(f"'$v' is not a valid date value. Dates must be in compact ISO-8601 format, e.g. '$example'")
    }
  }


  def getDefault()(implicit df: JWTReservedClaimValues, hv: JWTHeaderValues)
    = JWT(
    header = JWTHeader.getDefault,
    reserved = JWTReservedClaims.getDefault(),
    default = JWTDefaultClaims.getDefault,
    payload = Map()
  )

  def decipher(token: String)(implicit cvals: JWTCryptoValues, hv: JWTHeaderValues): Try[JWT] = {
    Try {
      val crypto = new Crypto(cvals.algorithm, cvals.secret)

      val cipher = Base64.getUrlDecoder.decode(token)
      val jwt: String = crypto.decrypt(cipher).payload

      getParts(jwt)
    }.flatMap { parts =>
      getJWTFromParts(parts._1, parts._2)
    }
  }

  private def getParts(jwt: String): (String, String, String) = jwt.split('.') match {
    case Array(header, payload, signature) => (header, payload, signature)
    case Array(header, payload) => (header, payload, "")
    case _ => throw new IllegalArgumentException("JWT could not be split into a header, payload, and signature")
  }

  private def getJWTFromParts(headerB64: String, payloadB64: String)(implicit cvals: JWTCryptoValues, hv: JWTHeaderValues): Try[JWT] = Try {
    val crypto = new Crypto(cvals.algorithm, cvals.secret)

    val headerStr: String = new String(Base64.getUrlDecoder.decode(headerB64.getBytes(crypto.charset)))
    val payloadStr: String = new String(Base64.getUrlDecoder.decode(payloadB64.getBytes(crypto.charset)))

    val jwtHeader: JWTHeader = headerStr.parseJson.convertTo[JWTHeader]
    val expectedAlg = Algorithm(jwtHeader.alg)

    require(expectedAlg == crypto.getAlgorithm, "JWT has a different algorithm from expected")

    val payloadFields = payloadStr.parseJson.asJsObject.fields

    var jWTReservedClaims = JWTReservedClaims()
    var jWTDefaultClaims = JWTDefaultClaims()

    val payloadFinal = payloadFields.filter { x =>
      x._1 match {
        case s if s == "exp" =>
          jWTReservedClaims = jWTReservedClaims.copy(exp = Some(x._2.convertTo[DateTime]))
          false

        case s if s == "nbf" =>
          jWTReservedClaims = jWTReservedClaims.copy(nbf = Some(x._2.convertTo[DateTime]))
          false

        case s if s == "iat" =>
          jWTReservedClaims = jWTReservedClaims.copy(iat = Some(x._2.convertTo[DateTime]))
          false

        case s if s == "iss" =>
          jWTReservedClaims = jWTReservedClaims.copy(iss = Some(x._2.convertTo[String]))
          false

        case s if s == "aud" =>
          jWTReservedClaims = jWTReservedClaims.copy(aud = Some(x._2.convertTo[String]))
          false

        case s if s == "prn" =>
          jWTReservedClaims = jWTReservedClaims.copy(prn = Some(x._2.convertTo[String]))
          false

        case s if s == "jti" =>
          jWTReservedClaims = jWTReservedClaims.copy(jti = Some(x._2.convertTo[String]))
          false

        case s if s == "typ" =>
          jWTReservedClaims = jWTReservedClaims.copy(typ = Some(x._2.convertTo[String]))
          false

        case _ =>
          true
      }
    }.filter { x =>
      x._1 match {
        case s if s == "application_id" =>
          jWTDefaultClaims = jWTDefaultClaims.copy(application_id = Some(x._2.convertTo[String]))
          false
        case s if s == "account_id" =>
          jWTDefaultClaims = jWTDefaultClaims.copy(account_id = Some(x._2.convertTo[String]))
          false
        case s if s == "session_id" =>
          jWTDefaultClaims = jWTDefaultClaims.copy(session_id = Some(x._2.convertTo[String]))
          false
        case s if s == "verify_id" =>
          jWTDefaultClaims = jWTDefaultClaims.copy(verify_id = Some(x._2.convertTo[String]))
          false
        case s if s == "context_id" =>
          jWTDefaultClaims = jWTDefaultClaims.copy(context_id = Some(x._2.convertTo[String]))
          false
        case s if s == "_any_json" =>
          jWTDefaultClaims = jWTDefaultClaims.copy(_any_json = Some(x._2.convertTo[String]))
          false
        case s if s == "isVerified" =>
          jWTDefaultClaims = jWTDefaultClaims.copy(isVerified = Some(x._2.convertTo[Boolean]))
          false
        case s if s == "isValidated" =>
          jWTDefaultClaims = jWTDefaultClaims.copy(isValidated = Some(x._2.convertTo[Boolean]))
          false
        case s if s == "isLive" =>
          jWTDefaultClaims = jWTDefaultClaims.copy(isLive = Some(x._2.convertTo[Boolean]))
          false
        case _ => true
      }
    }.map(x => (x._1, x._2.convertTo[String]))

    JWT(jwtHeader, jWTReservedClaims, jWTDefaultClaims, payloadFinal)
  }

  def validate(jwt: String)(implicit cvals: JWTCryptoValues, hv: JWTHeaderValues): Try[JWT] = {
    Try {
      // Extract the various parts of a JWT
      val parts: (String, String, String) = getParts(jwt)

      val headerB64 = parts._1
      val payloadB64 = parts._2
      val signatureB64 = parts._3

      val crypto = new Crypto(cvals.algorithm, cvals.secret)

      val prefix = headerB64 + "." + payloadB64
      val expectedB64Signature = crypto.getB64Signature(prefix)

      require(signatureB64 == expectedB64Signature, "Signature Does not Match")

      (headerB64, payloadB64)
    }.flatMap { h =>
      getJWTFromParts(h._1, h._2)
    }
  }

}