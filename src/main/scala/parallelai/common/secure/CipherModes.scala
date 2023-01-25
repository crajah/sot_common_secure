package parallelai.common.secure

import javax.crypto.Cipher

sealed trait CIPHER {
  def mode: Int
}

case object ENCRYPT extends CIPHER {
  val mode: Int = Cipher.ENCRYPT_MODE
}

case object DECRYPT extends CIPHER {
  val mode: Int = Cipher.DECRYPT_MODE
}