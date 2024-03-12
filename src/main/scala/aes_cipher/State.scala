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
      mixColumn(col)
      data.setCol(i, col)
    }
  }

  def invMixColumns(): Unit = {
    for (i <- 0 until Constants.N_B) {
      val col = data.getCol(i)
      invMixColumn(col)
      data.setCol(i, col)
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

  private def mixColumn(col: Array[Byte]): Unit = {
    val a = col(0)
    val b = col(1)
    val c = col(2)
    val d = col(3)
    col(0) = (galoisDouble((a ^ b).toByte) ^ b ^ c ^ d).toByte
    col(1) = (galoisDouble((b ^ c).toByte) ^ c ^ d ^ a).toByte
    col(2) = (galoisDouble((c ^ d).toByte) ^ d ^ a ^ b).toByte
    col(3) = (galoisDouble((d ^ a).toByte) ^ a ^ b ^ c).toByte
  }

  private def invMixColumn(col: Array[Byte]): Unit = {
    val a = col(0)
    val b = col(1)
    val c = col(2)
    val d = col(3)
    val x = galoisDouble((a ^ b ^ c ^ d).toByte)
    val y = galoisDouble((x ^ a ^ c).toByte)
    val z = galoisDouble((x ^ b ^ d).toByte)
    col(0) = (galoisDouble((y ^ a ^ b).toByte) ^ b ^ c ^ d).toByte
    col(1) = (galoisDouble((z ^ b ^ c).toByte) ^ c ^ d ^ a).toByte
    col(2) = (galoisDouble((y ^ c ^ d).toByte) ^ d ^ a ^ b).toByte
    col(3) = (galoisDouble((z ^ d ^ a).toByte) ^ a ^ b ^ c).toByte
  }

  private def galoisDouble(a: Byte): Byte = {
    var result = (a << 1).toByte
    if (a < 0) {
      result = (result ^ 0x1b).toByte
    }
    result
  }
}