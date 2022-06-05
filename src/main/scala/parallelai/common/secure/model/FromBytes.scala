package parallelai.common.secure.model

trait FromBytes[T] {
  def apply(a: Array[Byte]): T
}

object FromBytes {
  implicit val stringFromBytes: FromBytes[String] = new FromBytes[String] {
    def apply(a: Array[Byte]): String = new String(a)
  }

  def apply[T: FromBytes]: FromBytes[T] = implicitly[FromBytes[T]]
}