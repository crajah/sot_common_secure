package parallelai.common.secure

trait ToBytes[T] {
  def apply(t: T): Array[Byte]
}

object ToBytes {
  implicit val stringToBytes: ToBytes[String] = new ToBytes[String] {
    def apply(s: String): Array[Byte] = s.getBytes
  }

  implicit val bytesToBytes: ToBytes[Array[Byte]] = new ToBytes[Array[Byte]] {
    def apply(a: Array[Byte]): Array[Byte] = a
  }

  def apply[T: ToBytes]: ToBytes[T] = implicitly[ToBytes[T]]
}