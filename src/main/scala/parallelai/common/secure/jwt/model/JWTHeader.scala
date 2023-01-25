package parallelai.common.secure.jwt.model

import spray.json.DefaultJsonProtocol._
import spray.json._

case class JWTHeader(alg: String, typ: String)

trait JWTHeaderValues {
  val alg: String
}

object JWTHeader {
  implicit val rootJsonFormat: RootJsonFormat[JWTHeader] = jsonFormat2(JWTHeader.apply)

  def getDefault()(implicit hv: JWTHeaderValues) = JWTHeader(hv.alg, "JWT")
}