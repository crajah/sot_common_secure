package parallelai.common.secure

import javax.crypto.SecretKey
import org.apache.commons.lang3.SerializationUtils.deserialize

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

  implicit val secretKeyFromBytes: FromBytes[SecretKey] = new FromBytes[SecretKey] {
    def apply(a: Array[Byte]): SecretKey = deserialize[SecretKey](a)
  }

  def apply[T: FromBytes]: FromBytes[T] = implicitly[FromBytes[T]]
}