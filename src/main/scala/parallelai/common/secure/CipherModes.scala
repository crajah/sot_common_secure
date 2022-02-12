package parallelai.common.secure

import javax.crypto.Cipher

sealed trait CIPHER {
  def mode: Int
}
case object ENCRYPT extends CIPHER {
  def mode: Int = Cipher.ENCRYPT_MODE
}
case object DECRYPT extends CIPHER {
  def mode: Int = Cipher.DECRYPT_MODE
}

