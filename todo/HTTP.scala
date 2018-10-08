package todo

import java.net.{HttpURLConnection, URL}

import scala.collection.JavaConverters._

object HTTP
{
  def get(uri: String): Response = {
    send(Request("GET", uri))
  }

  def post(uri: String, body: String): Response = {
    send(Request("POST", uri, body))
  }

  def put(uri: String, body: String): Response = {
    send(Request("PUT", uri, body))
  }

  def delete(uri: String): Response = {
    send(Request("DELETE", uri))
  }

  def send(req: Request): Response = {
    val conn = new URL(req.uri).openConnection().asInstanceOf[HttpURLConnection]
    conn.setRequestMethod(req.method)

    if (req.body.nonEmpty) {
      conn.setDoOutput(true)
      conn.getOutputStream.write(req.body.getBytes("UTF-8"))
      conn.getOutputStream.close()
    }

    conn.connect()

    val body =
      if (conn.getContentLength > 0) {
        val str = new String(InputStreams.getBytes(conn.getInputStream), "UTF-8")
        str
      } else {
        ""
      }

    val headers = conn.getHeaderFields.asScala.filter(_._1 != null).foldLeft[Headers](Map.empty) {
      case (acc, (key, values)) => acc ++ Map(key -> values.asScala)
    }

    Response(conn.getResponseCode, body, headers)
  }

  type Headers = Map[String, Seq[String]]

  case class Request(method: String, uri: String, body: String = "")
  case class Response(status: Int, body: String = "", headers: Headers = Map.empty)
}

