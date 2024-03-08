package aes_cipher.aes_key

import aes_cipher.{Constants, State}

class AESKey(val data: Array[Int]) {

  def this(cipher_key: Array[Byte], isDirect: Boolean) = {
    this(new Array[Int](Constants.N_B * (Constants.N_R + 1)))
    if (isDirect) expandKey(cipher_key, data)
    else invExpandKey(cipher_key, data)
  }

  private def expandKey(cipher_key: Array[Byte], data: Array[Int]): Unit = {
    var temp: Int = 0
    var i: Int = 0

    while (i < Constants.N_K) {
      data(i) = java.nio.ByteBuffer.wrap(cipher_key.slice(4 * i, 4 * i + 4)).getInt
      i += 1
    }

    i = Constants.N_K

    while (i < Constants.N_B * (Constants.N_R + 1)) {
      temp = data(i - 1)
      if (i % Constants.N_K == 0) {
        temp = subWord(AESKey.rotWord(temp)) ^ Constants.R_CON(i / Constants.N_K - 1)
      }
      data(i) = data(i - Constants.N_K) ^ temp
      i += 1
    }
  }

  private def invExpandKey(cipher_key: Array[Byte], dword: Array[Int]): Unit = {
    expandKey(cipher_key, dword)

    for (round <- 1 until Constants.N_R) {
      val newWords = invMixColumnsWords(dword.slice(round * Constants.N_B, (round + 1) * Constants.N_B))
      for (i <- 0 until Constants.N_B) {
        dword(round * Constants.N_B + i) = newWords(i)
      }
    }
  }

  private def subWord(word: Int): Int = {
    var result = 0

    for (i <- 0 until 4) {
      val byte = AESKey.getByteFromWord(word, i)
      val newByte = AESKey.applySBox(byte)
      result |= (newByte & 0xFF) << (8 * i)
    }
    result
  }

  private def invMixColumnsWords(words: Array[Int]): Array[Int] = {
    val state = new State(words)
    state.invMixColumns()
    state.data.getCols.map(col => java.nio.ByteBuffer.wrap(col).getInt).slice(0, Constants.N_B)
  }
  
}

object AESKey {
  def rotWord(word: Int): Int = {
    (word << 8) | (word >>> 24)
  }

  def subWord(word: Int): Int = {
    var result = 0

    for (i <- 0 until 4) {
      val byte = getByteFromWord(word, i)
      val newByte = applySBox(byte)
      result |= (newByte & 0xFF) << (8 * i)
    }
    result
  }

  private def getByteFromWord(word: Int, pos: Int): Byte = {
    if (pos > 3) {
      throw new IllegalArgumentException("pos must be less than 4")
    }

    (word >>> (8 * pos)).toByte
  }

  private def applySBox(value: Byte): Byte = {
    val pos_x = (value >>> 4) & 0x0F
    val pos_y = value & 0x0F
    Constants.S_BOX(pos_x * 16 + pos_y)
  }
}
