import aes_cipher.Constants

@main def hello(): Unit =
  println("Hello world!")
  println(Constants.R_CON.mkString("Array(", ", ", ")"))
  println(msg)
  // Convert each byte to its hexadecimal representation and concatenate them
  val hexString: String = Constants.S_BOX.map(byte => f"${byte & 0xFF}%02x, ").mkString

  // Print the hexadecimal string
  println(hexString)
def msg = "I was compiled by Scala 3. :)"
