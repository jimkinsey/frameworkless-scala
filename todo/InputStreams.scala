package todo

import java.io.InputStream

object InputStreams
{
  def getBytes(inputStream: InputStream): Array[Byte] = {
    val buffer = new java.io.ByteArrayOutputStream
    val data = new Array[Byte](16384)
    var nRead: Int = 0
    while ( {
      nRead != -1
    }) {
      buffer.write(data, 0, nRead)
      nRead = inputStream.read(data, 0, data.length)
    }
    buffer.flush()

    buffer.toByteArray
  }
}
