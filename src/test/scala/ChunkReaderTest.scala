import utils.ChunkReader

import java.io.ByteArrayInputStream

class ChunkReaderTest extends munit.FunSuite {

  test("Test ChunkReader should read one chunk of exact size") {
    val input = new ByteArrayInputStream(Array.fill(16)(54.toByte))
    val reader = ChunkReader(input, 16, true)
    val buffer = Array.ofDim[Byte](1, 16)
    val chunksFilled = reader.readChunks(1, buffer) match {
      case Right(value) => value
      case Left(_) => fail("readChunks failed")
    }

    assertEquals(chunksFilled, 1)
    assertEquals(buffer(0).toList, List.fill(16)(54.toByte))
  }

  test("Test ChunkReader should read one chunk of partial size") {
    val input = new ByteArrayInputStream(Array.fill(8)(54.toByte))
    val reader = ChunkReader(input, 16, true)
    val buffer = Array.ofDim[Byte](1, 16)
    val chunksFilled = reader.readChunks(1, buffer) match {
      case Right(value) => value
      case Left(_) => fail("readChunks failed")
    }

    assertEquals(chunksFilled, 1)
    assertEquals(buffer(0).toList, List.fill(8)(54.toByte) ++ List.fill(8)(0.toByte))
  }

  test("Test ChunkReader should read multiple chunks of exact size") {
    val vec = Array.fill(16)(54.toByte) ++ Array.fill(16)(76.toByte)
    val input = new ByteArrayInputStream(vec)
    val reader = ChunkReader(input, 16, true)
    val buffer = Array.ofDim[Byte](2, 16)
    val chunksFilled = reader.readChunks(2, buffer) match {
      case Right(value) => value
      case Left(_) => fail("readChunks failed")
    }

    assertEquals(chunksFilled, 2)
    assertEquals(buffer(0).toList, List.fill(16)(54.toByte))
    assertEquals(buffer(1).toList, List.fill(16)(76.toByte))
  }

  test("Test ChunkReader should read multiple chunks of partial size") {
    val vec = Array.fill(8)(54.toByte) ++ Array.fill(8)(76.toByte) ++ Array.fill(8)(98.toByte)
    val input = new ByteArrayInputStream(vec)
    val reader = ChunkReader(input, 16, true)
    val buffer = Array.ofDim[Byte](2, 16)
    val chunksFilled = reader.readChunks(2, buffer) match {
      case Right(value) => value
      case Left(_) => fail("readChunks failed")
    }

    assertEquals(chunksFilled, 2)
    assertEquals(buffer(0).toList, List.fill(8)(54.toByte) ++ List.fill(8)(76.toByte))
    assertEquals(buffer(1).toList, List.fill(8)(98.toByte) ++ List.fill(8)(0.toByte))
  }

  test("Test ChunkReader should read more than available") {
    val input = new ByteArrayInputStream(Array.fill(16)(54.toByte))
    val reader = ChunkReader(input, 16, true)
    val buffer = Array.ofDim[Byte](2, 16)
    val chunksFilled = reader.readChunks(2, buffer) match {
      case Right(value) => value
      case Left(_) => fail("readChunks failed")
    }

    assertEquals(chunksFilled, 1)
    assertEquals(buffer(0).toList, List.fill(16)(54.toByte))
    assertEquals(buffer(1).toList, List.fill(16)(0.toByte))
  }
  

}
