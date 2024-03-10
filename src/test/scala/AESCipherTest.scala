import aes_cipher.{AESCipher, Constants}

class AESCipherTest extends munit.FunSuite {
  test("Cipher test should cipher correctly") {
    val plainBytes: Array[Byte] = Array(
      0x32, 0x43, 0xf6, 0xa8, 0x88, 0x5a, 0x30, 0x8d, 0x31, 0x31, 0x98, 0xa2, 0xe0, 0x37, 0x07, 0x34
    ).map(_.toByte)

    val cipherKey: Array[Byte] = Array(
      0x2b, 0x7e, 0x15, 0x16, 0x28, 0xae, 0xd2, 0xa6, 0xab, 0xf7, 0x15, 0x88, 0x09, 0xcf, 0x4f, 0x3c
    ).map(_.toByte)

    val expectedCipherBytes: Array[Byte] = Array(
      0x39, 0x25, 0x84, 0x1d, 0x02, 0xdc, 0x09, 0xfb, 0xdc, 0x11, 0x85, 0x97, 0x19, 0x6a, 0x0b, 0x32
    ).map(_.toByte)

    val cipher = new AESCipher(cipherKey)

    val cipherBytes = cipher.cipherBlock(plainBytes)

    for (i <- 0 until Constants.N_B * 4) {
      assertEquals(cipherBytes(i), expectedCipherBytes(i))
    }
  }

  test("Cipher test using BigInt") {
    val plainBytes: Array[Byte] = Array(
      0x32, 0x43, 0xf6, 0xa8, 0x88, 0x5a, 0x30, 0x8d, 0x31, 0x31, 0x98, 0xa2, 0xe0, 0x37, 0x07, 0x34
    ).map(_.toByte)

    val cipherKey: BigInt = BigInt("2b7e151628aed2a6abf7158809cf4f3c", 16)

    val expectedCipherBytes: Array[Byte] = Array(
      0x39, 0x25, 0x84, 0x1d, 0x02, 0xdc, 0x09, 0xfb, 0xdc, 0x11, 0x85, 0x97, 0x19, 0x6a, 0x0b, 0x32
    ).map(_.toByte)

    val cipher = AESCipher.apply(cipherKey)

    val cipherBytes = cipher.cipherBlock(plainBytes)

    for (i <- 0 until Constants.N_B * 4) {
      assertEquals(cipherBytes(i), expectedCipherBytes(i))
    }
  }

  test("Inv cipher should decrypt correctly") {
    val expectedPlainText: Array[Byte] = Array(
      0x32, 0x43, 0xf6, 0xa8, 0x88, 0x5a, 0x30, 0x8d, 0x31, 0x31, 0x98, 0xa2, 0xe0, 0x37, 0x07, 0x34
    ).map(_.toByte)

    val cipherKey: Array[Byte] = Array(
      0x2b, 0x7e, 0x15, 0x16, 0x28, 0xae, 0xd2, 0xa6, 0xab, 0xf7, 0x15, 0x88, 0x09, 0xcf, 0x4f, 0x3c
    ).map(_.toByte)

    val cipherBytes: Array[Byte] = Array(
      0x39, 0x25, 0x84, 0x1d, 0x02, 0xdc, 0x09, 0xfb, 0xdc, 0x11, 0x85, 0x97, 0x19, 0x6a, 0x0b, 0x32
    ).map(_.toByte)

    val cipher = new AESCipher(cipherKey)

    val plainText = cipher.invCipherBlock(cipherBytes)

    for (i <- 0 until Constants.N_B * 4) {
      assertEquals(plainText(i), expectedPlainText(i))
    }
  }

}
