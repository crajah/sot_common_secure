package parallelai.common.secure.jwt

import java.util.{Base64, UUID}

import scala.concurrent.Future
import scala.util.Try
import spray.json._
import org.joda.time.DateTime.now
import org.joda.time._
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import parallelai.common.secure._
import parallelai.common.secure.jwt.model._

//@deprecated(message = "Use JWT object directly with implicits", since = "29 October 2018")
//trait JWTSupport {
//  val iss: String
//  val aud: String
//  val prn: String
//  val typ: String
//
//  val secret: Array[Byte]
//  val algorithm: Algorithm
//
//  private val crypto = new Crypto(algorithm, secret)
//
//  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {
//    val formatter: DateTimeFormatter = ISODateTimeFormat.basicDateTime
//
//    def write(obj: DateTime): JsValue =
//      JsString(formatter.print(obj.withZone(DateTimeZone.UTC)))
//
//    def read(json: JsValue): DateTime = json match {
//      case JsString(s) => try {
//        formatter.parseDateTime(s).withZone(DateTimeZone.UTC)
//      } catch {
//        case _: Throwable => error(s)
//      }
//      case _ =>
//        error(json.toString())
//    }
//
//    def error(v: Any): DateTime = {
//      val example = formatter.print(0)
//      deserializationError(f"'$v' is not a valid date value. Dates must be in compact ISO-8601 format, e.g. '$example'")
//    }
//  }
//
//
//
//  def defaultJWTPrefixedToken(default: JWTDefaultClaims): String =
//    getPrefixedToken(defaultJWT(default))
//
//  def defaultJWT(default: JWTDefaultClaims): JWT = {
//    JWT(
//      JWTHeader(),
//      defaultReservedClaims,
//      default,
//      Map()
//    )
//  }
//
//  private def defaultReservedClaims: JWTReservedClaims = {
//    JWTReservedClaims(
//      exp = Some(now.plusDays(7)),
//      nbf = Some(now),
//      iat = Some(now),
//      iss = Some(iss),
//      aud = Some(aud),
//      prn = Some(prn),
//      jti = Some(UUID.randomUUID().toString),
//      typ = Some(typ)
//    )
//  }
//
//  def getPrefix(jwt: JWT): String = {
//    val headerJson = jwt.header.copy(alg = crypto.getAlgorithm.name).toJson.toString
//    val headerB64String = Base64.getUrlEncoder.encodeToString(headerJson.getBytes(crypto.charset))
//
//    val reservedClaimsJson = jwt.reserved.toJson
//    val defaulClaimsJson = jwt.default.toJson
//    val payloadMap = jwt.payload
//
//    val allClaims = reservedClaimsJson.asJsObject.fields ++ defaulClaimsJson.asJsObject.fields ++ payloadMap.map(p => { (p._1, JsString(p._2)) })
//    val claimsB64String = Base64.getUrlEncoder.encodeToString(JsObject(allClaims).toString.getBytes(crypto.charset))
//
//    headerB64String + "." + claimsB64String
//  }
//
//  def getSignature(jwt: JWT): String = {
//
//    val prefix = getPrefix(jwt)
//    val signature = crypto.getB64Signature(prefix)
//
//    signature
//  }
//
//  def getPrefixedToken(jwt: JWT): String = {
//    getPrefix(jwt) + "." + getSignature(jwt)
//  }
//
//  def validateJWTTokenFuture(jwt: String): Future[JWT] =
//    Future fromTry validateJWTToken(jwt)
//
//  def validateJWTToken(jwt: String): Try[JWT] = {
//    Try {
//      // Extract the various parts of a JWT
//      val parts: (String, String, String) = jwt.split('.') match {
//        case Array(header, payload, signature) => (header, payload, signature)
//        case Array(header, payload) => (header, payload, "")
//        case _ => throw new IllegalArgumentException("JWT could not be split into a header, payload, and signature")
//      }
//
//      val headerB64 = parts._1
//      val payloadB64 = parts._2
//      val signatureB64 = parts._3
//
//      val prefix = headerB64 + "." + payloadB64
//      val expectedB64Signature = crypto.getB64Signature(prefix)
//
//      require(signatureB64 == expectedB64Signature, "Signature Does not Match")
//
//      val headerStr: String = new String(Base64.getUrlDecoder.decode(headerB64.getBytes(crypto.charset)))
//      val payloadStr: String = new String(Base64.getUrlDecoder.decode(payloadB64.getBytes(crypto.charset)))
//
//      val jwtHeader: JWTHeader = headerStr.parseJson.convertTo[JWTHeader]
//      val expectedAlg = Algorithm(jwtHeader.alg)
//
//      require(expectedAlg == crypto.getAlgorithm, "JWT has a different algorithm from expected")
//
//      val payloadFields = payloadStr.parseJson.asJsObject.fields
//
//      var jWTReservedClaims = JWTReservedClaims()
//      var jWTDefaultClaims = JWTDefaultClaims()
//
//      val payloadFinal = payloadFields.filter { x =>
//        x._1 match {
//          case s if s == "exp" =>
//            jWTReservedClaims = jWTReservedClaims.copy(exp = Some(x._2.convertTo[DateTime]))
//            false
//
//          case s if s == "nbf" =>
//            jWTReservedClaims = jWTReservedClaims.copy(nbf = Some(x._2.convertTo[DateTime]))
//            false
//
//          case s if s == "iat" =>
//            jWTReservedClaims = jWTReservedClaims.copy(iat = Some(x._2.convertTo[DateTime]))
//            false
//
//          case s if s == "iss" =>
//            jWTReservedClaims = jWTReservedClaims.copy(iss = Some(x._2.convertTo[String]))
//            false
//
//          case s if s == "aud" =>
//            jWTReservedClaims = jWTReservedClaims.copy(aud = Some(x._2.convertTo[String]))
//            false
//
//          case s if s == "prn" =>
//            jWTReservedClaims = jWTReservedClaims.copy(prn = Some(x._2.convertTo[String]))
//            false
//
//          case s if s == "jti" =>
//            jWTReservedClaims = jWTReservedClaims.copy(jti = Some(x._2.convertTo[String]))
//            false
//
//          case s if s == "typ" =>
//            jWTReservedClaims = jWTReservedClaims.copy(typ = Some(x._2.convertTo[String]))
//            false
//
//          case _ =>
//            true
//        }
//      }.filter { x =>
//        x._1 match {
//          case s if s == "application_id" =>
//            jWTDefaultClaims = jWTDefaultClaims.copy(application_id = Some(x._2.convertTo[String]))
//            false
//          case s if s == "account_id" =>
//            jWTDefaultClaims = jWTDefaultClaims.copy(account_id = Some(x._2.convertTo[String]))
//            false
//          case s if s == "session_id" =>
//            jWTDefaultClaims = jWTDefaultClaims.copy(session_id = Some(x._2.convertTo[String]))
//            false
//          case s if s == "verify_id" =>
//            jWTDefaultClaims = jWTDefaultClaims.copy(verify_id = Some(x._2.convertTo[String]))
//            false
//          case s if s == "context_id" =>
//            jWTDefaultClaims = jWTDefaultClaims.copy(context_id = Some(x._2.convertTo[String]))
//            false
//          case s if s == "_any_json" =>
//            jWTDefaultClaims = jWTDefaultClaims.copy(_any_json = Some(x._2.convertTo[String]))
//            false
//          case s if s == "isVerified" =>
//            jWTDefaultClaims = jWTDefaultClaims.copy(isVerified = Some(x._2.convertTo[Boolean]))
//            false
//          case s if s == "isValidated" =>
//            jWTDefaultClaims = jWTDefaultClaims.copy(isValidated = Some(x._2.convertTo[Boolean]))
//            false
//          case s if s == "isLive" =>
//            jWTDefaultClaims = jWTDefaultClaims.copy(isLive = Some(x._2.convertTo[Boolean]))
//            false
//          case _ => true
//        }
//      }.map(x => (x._1, x._2.convertTo[String]))
//
//      JWT(jwtHeader, jWTReservedClaims, jWTDefaultClaims, payloadFinal)
//    }
//  }
//}

trait JWTWithKey extends KeySupport

trait JWTWithKeyAndHS265 extends JWTWithKey { JWTWithKey =>
  implicit val jwtAlgorithmAndKey = new JWTCryptoValues {
    override val secret = defaultSecret
  }
}

trait JWTMechanic extends JWTWithKey {
  implicit val reservedClaims = new JWTReservedClaimValues() {
    override val iss: String = "JWT Test"
    override val aud: String = iss
    override val prn: String = iss
    override val jti: String = iss
    override val typ: String = "test"
  }

  implicit val jwtAlgorithmAndKey = new JWTCryptoValues {
    override val secret = defaultSecret
  }

  implicit val headerValues = new JWTHeaderValues {
    override val alg: String = "HS256"
  }

  def getJWT(application_id: String, account_id: String, session_id: Option[String] = None, verify_id: Option[String] = None, context_id: Option[String] = None)
  = Some(JWT.getDefault().copy(default = JWTDefaultClaims(
    application_id = Some(application_id),
    account_id = Some(account_id),
    session_id = session_id,
    verify_id = verify_id,
    context_id = context_id
  )))
}