package utils

class Config(val inputFile: Option[String], val encryptedFile: Option[String], val decryptedFile: Option[String], val repeat: Option[Int])

object Config {
  def apply(): Config = {
    val inputFile = sys.env.getOrElse("PLAIN_TEXT", {
      println("INPUT_FILE environment variable not found")
      System.exit(1)
      ""
    })
    val encryptedFile = sys.env.getOrElse("ENCRYPTED_TEXT", {
      println("ENCRYPTED_TEXT environment variable not found")
      System.exit(1)
      ""
    })
    val decryptedFile = sys.env.getOrElse("DECRYPTED_TEXT", {
      println("DECRYPTED_TEXT environment variable not found")
      System.exit(1)
      ""
    })

    val repeat = sys.env.get("REPEAT").map(_.toInt).getOrElse {
      println("REPEAT environment variable not found")
      System.exit(1)
      0
    }
    new Config(Option(inputFile), Option(encryptedFile), Option(decryptedFile), Option(repeat))
  }
}