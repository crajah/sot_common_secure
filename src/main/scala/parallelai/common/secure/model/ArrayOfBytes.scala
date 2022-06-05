package parallelai.common.secure.model

trait ArrayOfBytes[T] {
  def apply(t: T): Array[Byte]
}

object ArrayOfBytes {
  implicit val stringAsArrayOfBytes: ArrayOfBytes[String] = _.getBytes

  def apply[T: ArrayOfBytes]: ArrayOfBytes[T] = implicitly[ArrayOfBytes[T]]
}