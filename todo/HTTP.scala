package todo

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

  def send(req: Request): Response = {
    val conn = new URL(req.uri).openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod(req.method)
    conn.connect()

    val body =
      if (conn.getContentLength > 0) {
        val str = new String(InputStreams.getBytes(conn.getInputStream), "UTF-8")
        str
      } else {
        ""
      }

    Response(conn.getResponseCode, body)
  }

  case class Request(method: String, uri: String)
  case class Response(status: Int, body: String = "")
}

