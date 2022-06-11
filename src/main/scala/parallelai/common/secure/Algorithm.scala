package parallelai.common.secure

sealed trait Algorithm {
  def name: String

  def value: String

  override def toString: String = name
}

object Algorithm {
  def apply(name: String): Algorithm = name match {
    case s if s == HS256.name => HS256
    case s if s == HS384.name => HS384
    case s if s == HS512.name => HS512
    case s if s == AES.name => AES
    case s if s == DES.name => DES
    case s if s == NONE.name => NONE
    case _ => throw new Exception("Unknown Algorithm")
  }
}

case object HS256 extends Algorithm {
  val name = "HS256"
  val value = "HmacSHA256"
}

case object HS384 extends Algorithm {
  val name = "HS384"
  val value = "HmacSHA384"
}

case object HS512 extends Algorithm {
  val name = "HS512"
  val value = "HmacSHA512"
}

case object NONE extends Algorithm {
  val name = "NONE"
  val value = "NONE"
}

case object AES extends Algorithm {
  val name = "AES"
  val value = "AES/CBC/PKCS5Padding"
}

case object DES extends Algorithm {
  val name = "DES"
  val value = "DES/CBC/PKCS5Padding"
}