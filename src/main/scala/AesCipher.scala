import aes_cipher.{AesBlockCipher, Constants}
import utils.{ChunkReader, ChunkWriter}

import java.io.{File, FileInputStream, FileOutputStream, InputStream, OutputStream}
import java.util.concurrent.ForkJoinPool
import scala.collection.parallel.CollectionConverters.*
import scala.collection.parallel.ForkJoinTaskSupport


class AesCipher(cipherKey: BigInt, nThreads: Int) {
  private val blockCipher = AesBlockCipher(cipherKey)
  private val forkJoinPool = new ForkJoinPool(nThreads)
  private val forkJoinTask = new ForkJoinTaskSupport(forkJoinPool)
  private val bufferSize = 1_000_000
  private val buffer = Array.ofDim[Byte](bufferSize, 4 * Constants.N_B)

  def cipherBlocks(chunks: Array[Array[Byte]]): Array[Array[Byte]] = {
    val cipheredChunks = chunks.par
    cipheredChunks.tasksupport = forkJoinTask
    cipheredChunks.map(block => blockCipher.cipherBlock(block)).toArray
  }

  def decipherBlocks(chunks: Array[Array[Byte]]): Array[Array[Byte]] = {
    val decipheredChunks = chunks.par
    decipheredChunks.tasksupport = forkJoinTask
    decipheredChunks.map(block => blockCipher.invCipherBlock(block)).toArray
  }

  def cipher(input: InputStream, output: OutputStream): Either[Exception, Unit] = {
    val chunkReader = ChunkReader(input, 4 * Constants.N_B, true)
    val chunkWriter = ChunkWriter(output, false)

    while (true) {
      chunkReader.readChunks(bufferSize, buffer) match {
        case Right(chunksFilled) =>
          if (chunksFilled == 0) return Right(())
          val cipheredChunks = cipherBlocks(buffer.slice(0, chunksFilled))
          chunkWriter.writeChunks(cipheredChunks)
        case Left(e) => return Left(e)
      }
    }

    Right(())
  }

  def decipher(input: InputStream, output: OutputStream): Either[Exception, Unit] = {
    val chunkReader = ChunkReader(input, 4 * Constants.N_B, false)
    val chunkWriter = ChunkWriter(output, true)

    while (true) {
      chunkReader.readChunks(bufferSize, buffer) match {
        case Right(chunksFilled) =>
          if (chunksFilled == 0) return Right(())
          val decipheredChunks = decipherBlocks(buffer.slice(0, chunksFilled))
          chunkWriter.writeChunks(decipheredChunks)
        case Left(e) => return Left(e)
      }
    }

    Right(())
  }

  def cipherFile(inputFile: String, outputFile: String): Either[Exception, Unit] = {
    val input = new FileInputStream(new File(inputFile))
    val output = new FileOutputStream(new File(outputFile))
    try {
      cipher(input, output)
    } finally {
      input.close()
      output.close()
    }
  }

  def decipherFile(inputFile: String, outputFile: String): Either[Exception, Unit] = {
    val input = new FileInputStream(new File(inputFile))
    val output = new FileOutputStream(new File(outputFile))
    try {
      decipher(input, output)
    } finally {
      input.close()
      output.close()
    }
  }
  
}

