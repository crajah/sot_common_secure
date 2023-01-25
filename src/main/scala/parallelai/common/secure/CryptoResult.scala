package parallelai.common.secure

case class CryptoResult[T](payload: T, params: Option[Array[Byte]] = None)