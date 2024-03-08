import aes_cipher.Constants

@main def hello(): Unit =
  println("Hello world!")
  println(Constants.R_CON.mkString("Array(", ", ", ")"))
  println(msg)

def msg = "I was compiled by Scala 3. :)"
