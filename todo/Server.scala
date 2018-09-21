package todo

import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpServer}
import todo.HTTP.{Request, Response}

object Server
{
  def start(port: Int, route: (Request) => (Response)): HttpServer = {
    val server = HttpServer.create(new InetSocketAddress(port), 0)

    server.createContext("/", (exchange: HttpExchange) => {
      val req = Request(exchange.getRequestMethod, exchange.getRequestURI.toString)
      val res = route(req)

      res.headers.foreach { case (name, values) =>
        values.foreach { value =>
          exchange.getResponseHeaders.add(name, value)
        }
      }

      if (res.body.nonEmpty) {
        val bytes = res.body.getBytes("UTF-8")
        exchange.sendResponseHeaders(res.status, bytes.length)
        val os = exchange.getResponseBody
        os.write(bytes)
        os.close()
      } else {
        exchange.sendResponseHeaders(res.status, -1)
      }
    })

    server.setExecutor(null)
    server.start()
    server
  }
}
