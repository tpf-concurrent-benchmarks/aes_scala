import com.timgroup.statsd.NonBlockingStatsDClientBuilder
import utils.Config

import java.io.{BufferedInputStream, FileInputStream, FileOutputStream, IOException}
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

  val config = Config()

  val cipherKey: BigInt = BigInt("2b7e151628aed2a6abf7158809cf4f3c", 16)
  println(s"Cipher key: ${cipherKey.toString(16)}")

  val cipher = new AesCipher(cipherKey, nThreads)

  val startTime = System.nanoTime()

  for (_ <- 0 until config.repeat.get) {
    runIteration(cipher, config) match {
      case Right(_) => 
      case Left(e) =>
        println(s"Error while encrypting/decrypting file: $e")
        System.exit(1)
    }
  }

  val elapsedTime = (System.nanoTime() - startTime) / 1e9
  println(s"Elapsed time: $elapsedTime s")

  val statsdClient = NonBlockingStatsDClientBuilder().prefix("aes_cipher").hostname("graphite").port(8125).build()
  println("Sending metrics to graphite")
  statsdClient.gauge("completion_time", elapsedTime)

def runIteration(cipher: AesCipher, config: Config): Either[IOException, Unit] = {
  (config.inputFile, config.encryptedFile, config.decryptedFile) match {
    case (Some(inputFile), Some(encryptedFile), Some(decryptedFile)) =>
      cipher.cipherFile(inputFile, encryptedFile)
      cipher.decipherFile(encryptedFile, decryptedFile)
      Right(())
    case (Some(inputFile), Some(encryptedFile), None) =>
      cipher.cipherFile(inputFile, encryptedFile)
      Right(())
    case (None, Some(encryptedFile), Some(decryptedFile)) =>
      cipher.decipherFile(encryptedFile, decryptedFile)
      Right(())
    case _ =>
      Left(new IOException("Invalid input"))
  }
}

