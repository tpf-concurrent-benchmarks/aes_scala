package aes_cipher

class Matrix(val data: Array[Array[Byte]]) {

  def this(rows: Int, cols: Int) = {
    this(Array.ofDim[Byte](rows, cols))
  }

  def get(row: Int, col: Int): Byte = {
    data(row)(col)
  }

  def set(row: Int, col: Int, value: Byte): Unit = {
    data(row)(col) = value
  }

  def getRowsAmount: Int = {
    data.length
  }

  def getColsAmount: Int = {
    data(0).length
  }

  def getRow(row: Int): Array[Byte] = {
    data(row)
  }

  def getCols: Array[Array[Byte]] = {
    data.transpose
  }

  def getCol(col: Int): Array[Byte] = {
    data.map(_(col))
  }

  def setCol(col: Int, data: Array[Byte]): Unit = {
    for (i <- data.indices) {
      this.data(i)(col) = data(i)
    }
  }

  def shiftRowLeft(row: Int, amount: Int): Unit = {
    for (_ <- 0 until amount) {
      val temp = data(row)(0)
      System.arraycopy(data(row), 1, data(row), 0, data(row).length - 1)
      data(row)(data(row).length - 1) = temp
    }
  }

  def shiftRowRight(row: Int, amount: Int): Unit = {
    for (_ <- 0 until amount) {
      val temp = data(row)(data(row).length - 1)
      System.arraycopy(data(row), 0, data(row), 1, data(row).length - 1)
      data(row)(0) = temp
    }
  }
}