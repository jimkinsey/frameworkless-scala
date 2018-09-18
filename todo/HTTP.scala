package todo

import java.io.InputStream
import java.net.{HttpURLConnection, URL}

object HTTP
{
  def get(uri: String): Response = {
    send(Request("GET", uri))
  }

  def put(uri: String): Response = {
    send(Request("PUT", uri))
  }

  def delete(uri: String): Response = {
    send(Request("DELETE", uri))
  }

  def send(req: Request) = {
    val conn = new URL(req.uri).openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod(req.method)
    conn.connect()

    val body =
      if (conn.getContentLength > 0) {
        val str = new String(getBytes(conn.getInputStream), "UTF-8")
        str
      } else {
        ""
      }

    Response(conn.getResponseCode, body)
  }

  case class Request(method: String, uri: String)
  case class Response(status: Int, body: String = "")

  private def getBytes(inputStream: InputStream): Array[Byte] = {
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