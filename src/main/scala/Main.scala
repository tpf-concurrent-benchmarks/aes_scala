import java.io.{BufferedInputStream, FileInputStream, FileOutputStream}
import scala.concurrent.duration.DurationInt

import scala.util.{Try, Using}

@main def main(): Unit =

  val nThreads = sys.env.get("N_THREADS").map(_.toInt).getOrElse {
    println("N_THREADS environment variable not found")
    System.exit(1)
    0
  }

  println(s"Number of threads: $nThreads")

  Thread.sleep(10.seconds.toMillis)

  val inputFile = "test_files/lorem_ipsum.txt"
  val outputFile = "test_files/output.txt"
  val decryptedFile = "test_files/decrypted.txt"

  val cipherKey: BigInt = BigInt("2b7e151628aed2a6abf7158809cf4f3c", 16)
  println(s"Cipher key: ${cipherKey.toString(16)}")

  val cipher = new AesCipher(cipherKey, nThreads)

  val startTime = System.nanoTime()

  Try {
    Using.resources(new FileInputStream(inputFile), new FileOutputStream(outputFile)) { (input, output) =>
      cipher.cipher(input, output)
    }
  } match {
    case scala.util.Failure(e) =>
      println(s"Error while encrypting file: ${e.getMessage}")
      System.exit(1)
    case _ =>
  }

  Try {
    Using.resources(new FileInputStream(outputFile), new FileOutputStream(decryptedFile)) { (input, output) =>
      cipher.decipher(input, output)
    }
  } match {
    case scala.util.Failure(e) =>
      println(s"Error while decrypting file: ${e.getMessage}")
      System.exit(1)
    case _ =>
  }

  val elapsedTime = (System.nanoTime() - startTime) / 1e9
  println(s"Elapsed time: $elapsedTime s")

  if (compareFiles(inputFile, decryptedFile)) {
    println("Test passed")
  } else {
    println("Test failed")
  }

def compareFiles(file1: String, file2: String): Boolean = {
  val buffer1 = new Array[Byte](1024)
  val buffer2 = new Array[Byte](1024)

  Using.Manager { use =>
    val input1 = use(new BufferedInputStream(new FileInputStream(file1)))
    val input2 = use(new BufferedInputStream(new FileInputStream(file2)))

    Iterator.continually((input1.read(buffer1), input2.read(buffer2))).takeWhile {
      case (-1, -1) => false
      case (bytesRead1, bytesRead2) => bytesRead1 == bytesRead2 && buffer1.slice(0, bytesRead1).sameElements(buffer2.slice(0, bytesRead2))
    }.hasNext
  }.getOrElse(false)
}

