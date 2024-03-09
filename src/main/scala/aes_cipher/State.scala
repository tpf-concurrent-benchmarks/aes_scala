package aes_cipher

import aes_cipher.Constants.{INV_S_BOX, S_BOX}

class State(var data: Matrix) {

  def this() = {
    this(new Matrix(4, Constants.N_B))
  }

  def this(data_in: Array[Byte]) = {
    this()
    for (i <- 0 until Constants.N_B) {
      val col = Array(data_in(4 * i), data_in(4 * i + 1), data_in(4 * i + 2), data_in(4 * i + 3))
      data.setCol(i, col)
    }
  }

  def this(words: Array[Int]) = {
    this()
    for (i <- 0 until Constants.N_B) {
      val word = words(i)
      val wordBytes = java.nio.ByteBuffer.allocate(4).putInt(word).array()
      val col = Array(wordBytes(0), wordBytes(1), wordBytes(2), wordBytes(3))
      data.setCol(i, col)
    }
  }

  def setDataOut(data_out: Array[Byte]): Unit = {
    for (i <- 0 until Constants.N_B) {
      val col = data.getCol(i)
      data_out(4 * i) = col(0)
      data_out(4 * i + 1) = col(1)
      data_out(4 * i + 2) = col(2)
      data_out(4 * i + 3) = col(3)
    }
  }

  def subBytes(): Unit = {
    applySubstitution(S_BOX)
  }

  def invSubBytes(): Unit = {
    applySubstitution(INV_S_BOX)
  }

  private def applySubstitution(sub_box: Array[Byte]): Unit = {
    for (row <- 0 until data.getRowsAmount; col <- 0 until data.getColsAmount) {
      val value = data.get(row, col)
      data.set(row, col, sub_box(value & 0xFF))
    }
  }

  def shiftRows(): Unit = {
    for (i <- 1 until data.getRowsAmount) {
      data.shiftRowLeft(i, i)
    }
  }

  def invShiftRows(): Unit = {
    for (i <- 1 until data.getRowsAmount) {
      data.shiftRowRight(i, i)
    }
  }

  def mixColumns(): Unit = {
    for (i <- 0 until Constants.N_B) {
      val col = data.getCol(i)
      val newCol = Array[Byte](
        (galoisMul(col(0), 2) ^ galoisMul(col(1), 3) ^ col(2) ^ col(3)).toByte,
        (col(0) ^ galoisMul(col(1), 2) ^ galoisMul(col(2), 3) ^ col(3)).toByte,
        (col(0) ^ col(1) ^ galoisMul(col(2), 2) ^ galoisMul(col(3), 3)).toByte,
        (galoisMul(col(0), 3) ^ col(1) ^ col(2) ^ galoisMul(col(3), 2)).toByte
      )

      data.setCol(i, newCol)
    }
  }

  def invMixColumns(): Unit = {
    for (i <- 0 until Constants.N_B) {
      val col = data.getCol(i)
      val newCol = Array[Byte](
        (galoisMul(col(0), 14) ^ galoisMul(col(1), 11) ^ galoisMul(col(2), 13) ^ galoisMul(col(3), 9)).toByte,
        (galoisMul(col(0), 9) ^ galoisMul(col(1), 14) ^ galoisMul(col(2), 11) ^ galoisMul(col(3), 13)).toByte,
        (galoisMul(col(0), 13) ^ galoisMul(col(1), 9) ^ galoisMul(col(2), 14) ^ galoisMul(col(3), 11)).toByte,
        (galoisMul(col(0), 11) ^ galoisMul(col(1), 13) ^ galoisMul(col(2), 9) ^ galoisMul(col(3), 14)).toByte
      )
      data.setCol(i, newCol)
    }
  }

  def addRoundKey(roundKey: Array[Int]): Unit = {
    for (i <- 0 until Constants.N_B) {
      val col = data.getCol(i)
      val word = roundKey(i)
      val wordBytes = java.nio.ByteBuffer.allocate(4).putInt(word).array()
      val newCol = Array(
        (col(0) ^ wordBytes(0)).toByte,
        (col(1) ^ wordBytes(1)).toByte,
        (col(2) ^ wordBytes(2)).toByte,
        (col(3) ^ wordBytes(3)).toByte
      )
      data.setCol(i, newCol)
    }
  }

  private def galoisMul(a: Byte, b: Byte): Byte = {
    var result = 0
    var tempA = a & 0xFF
    var tempB = b & 0xFF
    while (tempB != 0) {
      if ((tempB & 1) != 0) {
        result ^= tempA
      }
      val hiBitSet = (tempA & 0x80) != 0
      tempA <<= 1
      if (hiBitSet) {
        tempA ^= 0x1b
      }
      tempB >>= 1
    }
    result.toByte
  }
}