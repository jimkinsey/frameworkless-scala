package todo

import java.net.{HttpURLConnection, URL}

object HTTP
{
  def get(uri: String): Response = {
    val conn = new URL(uri).openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("GET")
    conn.connect()

    Response(conn.getResponseCode)
  }

  def put(uri: String): Response = {
    val conn = new URL(uri).openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod("PUT")
    conn.connect()

    Response(conn.getResponseCode)
  }

  case class Response(status: Int)
}