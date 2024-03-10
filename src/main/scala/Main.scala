import aes_cipher.{AESCipher, Constants}

import scala.util.Random

@main def main(): Unit =

  val blocksToEncrypt = 1000000
  val blocks = (0 until blocksToEncrypt).map(_ => {
    val block = new Array[Byte](4 * Constants.N_B)
    for (i <- 0 until (4 * Constants.N_B)) {
      block(i) = Random.nextInt(256).toByte
    }
    block
  }).toVector

  val cipherKey: BigInt = BigInt("2b7e151628aed2a6abf7158809cf4f3c", 16)

  val cipher = AESCipher(cipherKey)

  val startTime = System.nanoTime()

  if (!sys.env.contains("LOCAL")) {

    val result = applyOperationsAndCompare(cipher, blocks)
    assert(result)

    println("Test passed (local)")

    val elapsedTime = (System.nanoTime() - startTime) / 1e9
    println(s"Elapsed time: $elapsedTime s")

  }

def applyOperationsAndCompare(cipher: AESCipher, blocks: Vector[Array[Byte]]): Boolean = {
  val cipheredBlocks = blocks.map(block => cipher.cipherBlock(block))

  val decipheredBlocks = cipheredBlocks.map(block => cipher.invCipherBlock(block))

  blocks.zip(decipheredBlocks).forall { case (originalBlock, decipheredBlock) =>
    originalBlock.sameElements(decipheredBlock)
  }
}

