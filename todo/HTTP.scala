package todo

import java.net.{HttpURLConnection, URL}
import scala.collection.JavaConverters._

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

    val headers = conn.getHeaderFields.asScala.filter(_._1 != null).foldLeft[Map[String, Seq[String]]](Map.empty) {
      case (acc, (key, values)) => acc ++ Map(key -> values.asScala)
    }

    Response(conn.getResponseCode, body, headers)
  }

  case class Request(method: String, uri: String)
  case class Response(status: Int, body: String = "", headers: Map[String, Seq[String]] = Map.empty)
}

