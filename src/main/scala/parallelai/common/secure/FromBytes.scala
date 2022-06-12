package parallelai.common.secure

trait FromBytes[T] {
  def apply(a: Array[Byte]): T
}

object FromBytes {
  implicit val stringFromBytes: FromBytes[String] = new FromBytes[String] {
    def apply(a: Array[Byte]): String = new String(a)
  }

  implicit val intFromBytes: FromBytes[Int] = new FromBytes[Int] {
    def apply(a: Array[Byte]): Int = new String(a).toInt
  }

  implicit val bytesFromBytes: FromBytes[Array[Byte]] = new FromBytes[Array[Byte]] {
    def apply(a: Array[Byte]): Array[Byte] = a
  }

  def apply[T: FromBytes]: FromBytes[T] = implicitly[FromBytes[T]]
}