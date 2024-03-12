package utils

import java.io.{BufferedInputStream, InputStream}
import scala.annotation.tailrec

class ChunkReader[T <: InputStream](input: T, chunkSize: Int, withPadding: Boolean) {
  private val reader = new BufferedInputStream(input)

  def readChunks(chunksAmount: Int, buffer: Array[Array[Byte]]): Either[Exception, Int] = {
    @tailrec
    def loop(chunksFilled: Int): Either[Exception, Int] = {
      if (chunksFilled < chunksAmount) {
        fillChunk(buffer(chunksFilled)) match {
          case Right(0) => Right(chunksFilled)
          case Right(n) =>
            if (n < chunkSize) Right(chunksFilled + 1)
            else loop(chunksFilled + 1)
          case Left(e) => Left(e)
        }
      } else Right(chunksFilled)
    }

    loop(0)
  }

  def fillChunk(buffer: Array[Byte]): Either[Exception, Int] = {
    def loop(bytesRead: Int): Either[Exception, Int] = {
      if (bytesRead < chunkSize) {
        try {
          val n = reader.read(buffer, bytesRead, buffer.length - bytesRead)
          if (n == -1) {
            if (withPadding) applyNullPadding(bytesRead, buffer)
            Right(bytesRead)
          } else loop(bytesRead + n)
        } catch {
          case e: Exception => Left(e)
        }
      } else Right(bytesRead)
    }

    loop(0)
  }

  private def applyNullPadding(from: Int, buffer: Array[Byte]): Unit = {
    for (i <- from until chunkSize) buffer(i) = 0
  }
}

object ChunkReader {
  def apply[T <: InputStream](input: T, chunkSize: Int, withPadding: Boolean): ChunkReader[T] =
    new ChunkReader(input, chunkSize, withPadding)
}
