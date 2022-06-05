package parallelai.common.secure.model

trait ToBytes[T] {
  def apply(t: T): Array[Byte]
}

object ToBytes {
  implicit val stringToBytes: ToBytes[String] = new ToBytes[String] {
    def apply(s: String): Array[Byte] = s.getBytes
  }

  def apply[T: ToBytes]: ToBytes[T] = implicitly[ToBytes[T]]
}