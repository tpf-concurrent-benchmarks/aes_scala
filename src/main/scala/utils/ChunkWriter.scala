package utils

import java.io.{BufferedOutputStream, OutputStream}

class ChunkWriter (output: OutputStream, removePadding: Boolean) {
  private val bufferedOutput = new BufferedOutputStream(output)

  def writeChunks(chunks: Array[Array[Byte]]): Either[Exception, Unit] = {
    try {
      for (chunk <- chunks) {
        writeChunk(chunk)
      }
      Right(())
    } catch {
      case e: Exception => Left(e)
    }
  }

  private def writeChunk(chunk: Array[Byte]): Unit = {
    if (removePadding) {
      writeChunkWithoutPadding(chunk)
    } else {
      bufferedOutput.write(chunk)
    }
    bufferedOutput.flush()
  }

  private def writeChunkWithoutPadding(chunk: Array[Byte]): Unit = {
    val paddingPos = chunk.indexWhere(_ == 0) match {
      case -1 => chunk.length
      case pos => pos
    }
    bufferedOutput.write(chunk, 0, paddingPos)
  }

  def close(): Unit = {
    bufferedOutput.flush()
    bufferedOutput.close()
  }
}

object ChunkWriter {
  def apply(output: OutputStream, removePadding: Boolean): ChunkWriter =
    new ChunkWriter(output, removePadding)
}
