package parallelai.common.secure.model

trait ArrayOfBytes[T] {
  def apply(t: T): Array[Byte]
}

object ArrayOfBytes {
  implicit val stringAsArrayOfBytes: ArrayOfBytes[String] = new ArrayOfBytes[String] {
    def apply(s: String): Array[Byte] = s.getBytes
  }

  def apply[T: ArrayOfBytes]: ArrayOfBytes[T] = implicitly[ArrayOfBytes[T]]
}