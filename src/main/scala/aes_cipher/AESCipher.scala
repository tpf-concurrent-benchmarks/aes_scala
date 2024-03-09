package aes_cipher

import aes_cipher.aes_key.AESKey
import java.nio.ByteBuffer

class AESCipher(val expandedKey: AESKey, val invExpandedKey: AESKey) {

  def this(cipherKey: Array[Byte]) = {
    this(new AESKey(cipherKey, true), new AESKey(cipherKey, false))
  }

  def this(cipherKey: Long) = {
    this(ByteBuffer.allocate(8).putLong(cipherKey).array())
  }

  def cipherBlock(dataIn: Array[Byte]): Array[Byte] = {
    val dataOut = new Array[Byte](4 * Constants.N_B)

    val state = new State(dataIn)

    state.addRoundKey(expandedKey.data.slice(0, Constants.N_B))

    for (round <- 1 until Constants.N_R) {
      state.subBytes()
      state.shiftRows()
      state.mixColumns()
      state.addRoundKey(expandedKey.data.slice(round * Constants.N_B, (round + 1) * Constants.N_B))
    }

    state.subBytes()
    state.shiftRows()
    state.addRoundKey(expandedKey.data.slice(Constants.N_R * Constants.N_B, (Constants.N_R + 1) * Constants.N_B))

    state.setDataOut(dataOut)

    dataOut
  }

  def invCipherBlock(dataIn: Array[Byte]): Array[Byte] = {
    val dataOut = new Array[Byte](4 * Constants.N_B)

    val state = new State(dataIn)

    state.addRoundKey(invExpandedKey.data.slice(Constants.N_R * Constants.N_B, (Constants.N_R + 1) * Constants.N_B))

    for (round <- Constants.N_R - 1 to 1 by -1) {
      state.invSubBytes()
      state.invShiftRows()
      state.invMixColumns()
      state.addRoundKey(invExpandedKey.data.slice(round * Constants.N_B, (round + 1) * Constants.N_B))
    }

    state.invSubBytes()
    state.invShiftRows()
    state.addRoundKey(invExpandedKey.data.slice(0, Constants.N_B))

    state.setDataOut(dataOut)

    dataOut
  }
}

object AESCipher {
  def apply(cipherKey: BigInt): AESCipher = {
    val cipherKeyBytes = cipherKey.toByteArray
    val cipherKeyArray = new Array[Byte](4 * Constants.N_B)
    Array.copy(cipherKeyBytes, 0, cipherKeyArray, 0, 4 * Constants.N_B)
    new AESCipher(cipherKeyArray)
  }
}
