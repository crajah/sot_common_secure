package parallelai.common.secure.jwt.model

import java.util.Base64

import cats.Monad
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}
import org.joda.time._
import org.joda.time.DateTime.now
import parallelai.common.secure.Algorithm
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.util.Try

trait JWTReservedClaimValues {
  val iss: String
  val aud: String
  val prn: String
  val jti: String
  val typ: String
}

case class JWTReservedClaims(
   exp: Option[DateTime]  = None  // Expiry
  , nbf: Option[DateTime] = None                     // Not Before
  , iat: Option[DateTime] = None                     // Issued At
  , iss: Option[String] = None                            // Issuer
  , aud: Option[String] = None                            // Audience
  , prn: Option[String] = None                            // Prinicpal
  , jti: Option[String] = None                            // JWT ID
  , typ: Option[String] = None                            // Type of Contents
)

object JWTReservedClaims {
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


  implicit val rootJsonFormat: RootJsonFormat[JWTReservedClaims] = jsonFormat8(JWTReservedClaims.apply)

  def getDefault()(implicit df: JWTReservedClaimValues) = {
    JWTReservedClaims(
      iss = Some(df.iss),
      aud = Some(df.aud),
      prn = Some(df.prn),
      jti = Some(df.jti),
      typ = Some(df.typ)
    )
  }

}