package parallelai.common.secure.jwt.model

import spray.json.DefaultJsonProtocol._
import spray.json._

case class JWTDefaultClaims(
  application_id: Option[String] = None,
  account_id: Option[String] = None,
  session_id: Option[String] = None,
  verify_id: Option[String] = None,
  context_id: Option[String] = None,
  isVerified: Option[Boolean] = None,
  isValidated: Option[Boolean] = None,
  isLive: Option[Boolean] = None,
  _any_json: Option[String] = None
)

object JWTDefaultClaims {
  implicit val rootJsonFormat: RootJsonFormat[JWTDefaultClaims] = jsonFormat9(JWTDefaultClaims.apply)

  def getDefault = JWTDefaultClaims()

}