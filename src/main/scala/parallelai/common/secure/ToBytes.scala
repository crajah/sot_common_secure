package parallelai.common.secure

import javax.crypto.SecretKey
import org.apache.commons.lang3.SerializationUtils.serialize

trait ToBytes[T] {
  def apply(t: T): Array[Byte]
}

object ToBytes {
  implicit val stringToBytes: ToBytes[String] = new ToBytes[String] {
    def apply(s: String): Array[Byte] = s.getBytes
  }

  implicit val intToBytes: ToBytes[Int] = new ToBytes[Int] {
    def apply(s: Int): Array[Byte] = s.toString.getBytes
  }

  implicit val bytesToBytes: ToBytes[Array[Byte]] = new ToBytes[Array[Byte]] {
    def apply(a: Array[Byte]): Array[Byte] = a
  }

  implicit val secretKey: ToBytes[SecretKey] = new ToBytes[SecretKey] {
    def apply(s: SecretKey): Array[Byte] = serialize(s)
  }

  def apply[T: ToBytes]: ToBytes[T] = implicitly[ToBytes[T]]
}