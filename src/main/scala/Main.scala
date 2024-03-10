import aes_cipher.{AESCipher, Constants}

import java.util.concurrent.ForkJoinPool
import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.ForkJoinTaskSupport
import scala.concurrent.duration.*
import scala.util.Random

@main def main(): Unit =

  val nThreads = sys.env.get("N_THREADS").map(_.toInt).getOrElse {
    println("N_THREADS environment variable not found")
    System.exit(1)
    0
  }

  Thread.sleep(10.seconds.toMillis)

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
  val forkJoinPool = createForkJoinPool(nThreads)
  val forkJoinTask = new ForkJoinTaskSupport(forkJoinPool)

  val startTime = System.nanoTime()

  if (!sys.env.contains("LOCAL")) {

    val result = applyOperationsAndCompare(cipher, blocks, forkJoinTask)
    assert(result)

    println("Test passed (local)")

    val elapsedTime = (System.nanoTime() - startTime) / 1e9
    println(s"Elapsed time: $elapsedTime s")

  }

def applyOperationsAndCompare(cipher: AESCipher, blocks: Vector[Array[Byte]], forkJoinTask: ForkJoinTaskSupport): Boolean = {
  val cipheredBlocks = blocks.par
  cipheredBlocks.tasksupport = forkJoinTask
  cipheredBlocks.map(block => cipher.cipherBlock(block)).toVector

  val decipheredBlocks = cipheredBlocks.par
  decipheredBlocks.tasksupport = forkJoinTask
  decipheredBlocks.map(block => cipher.invCipherBlock(block)).toVector

  blocks.zip(decipheredBlocks).forall { case (originalBlock, decipheredBlock) =>
    originalBlock.sameElements(decipheredBlock)
  }
}

def createForkJoinPool(parallelismLevel: Int): java.util.concurrent.ForkJoinPool =
  new java.util.concurrent.ForkJoinPool(parallelismLevel)

