import aes_cipher.{Matrix, State}

class StateTest extends munit.FunSuite {
  test("Shift rows correctly") {
    val state = new State(Matrix(Array(
      Array(0xd4.toByte, 0xe0.toByte, 0xb8.toByte, 0x1e.toByte),
      Array(0xbf.toByte, 0xb4.toByte, 0x41.toByte, 0x27.toByte),
      Array(0x5d.toByte, 0x52.toByte, 0x11.toByte, 0x98.toByte),
      Array(0x30.toByte, 0xae.toByte, 0xf1.toByte, 0xe5.toByte)
    )))

    val expectedState = new State(Matrix(Array(
      Array(0xd4.toByte, 0xe0.toByte, 0xb8.toByte, 0x1e.toByte),
      Array(0xb4.toByte, 0x41.toByte, 0x27.toByte, 0xbf.toByte),
      Array(0x11.toByte, 0x98.toByte, 0x5d.toByte, 0x52.toByte),
      Array(0xe5.toByte, 0x30.toByte, 0xae.toByte, 0xf1.toByte)
    )))

    state.shiftRows()

    for (i <- 0 until 4) {
      assertEquals(state.data.getRow(i).toSeq, expectedState.data.getRow(i).toSeq)
    }
  }

  test("Inverse shift rows correctly") {
    val state = new State(Matrix(Array(
      Array(0xd4.toByte, 0xe0.toByte, 0xb8.toByte, 0x1e.toByte),
      Array(0xbf.toByte, 0xb4.toByte, 0x41.toByte, 0x27.toByte),
      Array(0x5d.toByte, 0x52.toByte, 0x11.toByte, 0x98.toByte),
      Array(0x30.toByte, 0xae.toByte, 0xf1.toByte, 0xe5.toByte)
    )))

    val expectedState = new State(Matrix(Array(
      Array(0xd4.toByte, 0xe0.toByte, 0xb8.toByte, 0x1e.toByte),
      Array(0x27.toByte, 0xbf.toByte, 0xb4.toByte, 0x41.toByte),
      Array(0x11.toByte, 0x98.toByte, 0x5d.toByte, 0x52.toByte),
      Array(0xae.toByte, 0xf1.toByte, 0xe5.toByte, 0x30.toByte)
    )))

    state.invShiftRows()

    for (i <- 0 until 4) {
      assertEquals(state.data.getRow(i).toSeq, expectedState.data.getRow(i).toSeq)
    }
  }

  test("SubBytes correctly") {
    val state = new State(Matrix(Array(
      Array(0x19.toByte, 0xa0.toByte, 0x9a.toByte, 0xe9.toByte),
      Array(0x3d.toByte, 0xf4.toByte, 0xc6.toByte, 0xf8.toByte),
      Array(0xe3.toByte, 0xe2.toByte, 0x8d.toByte, 0x48.toByte),
      Array(0xbe.toByte, 0x2b.toByte, 0x2a.toByte, 0x08.toByte)
    )))

    val expectedState = new State(Matrix(Array(
      Array(0xd4.toByte, 0xe0.toByte, 0xb8.toByte, 0x1e.toByte),
      Array(0x27.toByte, 0xbf.toByte, 0xb4.toByte, 0x41.toByte),
      Array(0x11.toByte, 0x98.toByte, 0x5d.toByte, 0x52.toByte),
      Array(0xae.toByte, 0xf1.toByte, 0xe5.toByte, 0x30.toByte)
    )))

    state.subBytes()

    for (i <- 0 until 4) {
      assertEquals(state.data.getRow(i).toSeq, expectedState.data.getRow(i).toSeq)
    }
  }

  test("Inverse sub bytes should work correctly") {
    val state = new State(Matrix(Array(
      Array(0xd4.toByte, 0xe0.toByte, 0xb8.toByte, 0x1e.toByte),
      Array(0x27.toByte, 0xbf.toByte, 0xb4.toByte, 0x41.toByte),
      Array(0x11.toByte, 0x98.toByte, 0x5d.toByte, 0x52.toByte),
      Array(0xae.toByte, 0xf1.toByte, 0xe5.toByte, 0x30.toByte)
    )))

    val expectedState = new State(Matrix(Array(
      Array(0x19.toByte, 0xa0.toByte, 0x9a.toByte, 0xe9.toByte),
      Array(0x3d.toByte, 0xf4.toByte, 0xc6.toByte, 0xf8.toByte),
      Array(0xe3.toByte, 0xe2.toByte, 0x8d.toByte, 0x48.toByte),
      Array(0xbe.toByte, 0x2b.toByte, 0x2a.toByte, 0x08.toByte)
    )))

    state.invSubBytes()

    for (i <- 0 until 4) {
      assertEquals(state.data.getRow(i).toSeq, expectedState.data.getRow(i).toSeq)
    }
  }

  test("Mix columns should work correctly") {
    val state = new State(Matrix(Array(
      Array(0xdb.toByte, 0xf2.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0x13.toByte, 0x0a.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0x53.toByte, 0x22.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0x45.toByte, 0x5c.toByte, 0x01.toByte, 0xc6.toByte)
    )))

    val expectedState = new State(Matrix(Array(
      Array(0x8e.toByte, 0x9f.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0x4d.toByte, 0xdc.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0xa1.toByte, 0x58.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0xbc.toByte, 0x9d.toByte, 0x01.toByte, 0xc6.toByte)
    )))

    state.mixColumns()

    for (i <- 0 until 4) {
      assertEquals(state.data.getRow(i).toSeq, expectedState.data.getRow(i).toSeq)
    }
  }

  test("Inv mix columns") {
    val state = new State(Matrix(Array(
      Array(0x8e.toByte, 0x9f.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0x4d.toByte, 0xdc.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0xa1.toByte, 0x58.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0xbc.toByte, 0x9d.toByte, 0x01.toByte, 0xc6.toByte)
    )))

    val expectedState = new State(Matrix(Array(
      Array(0xdb.toByte, 0xf2.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0x13.toByte, 0x0a.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0x53.toByte, 0x22.toByte, 0x01.toByte, 0xc6.toByte),
      Array(0x45.toByte, 0x5c.toByte, 0x01.toByte, 0xc6.toByte)
    )))

    state.invMixColumns()

    for (i <- 0 until 4) {
      assertEquals(state.data.getRow(i).toSeq, expectedState.data.getRow(i).toSeq)
    }
  }
}
