package todo

import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpServer}
import todo.HTTP.Request

object Server
{
    def start(port: Int): HttpServer = {
        val store = new Store()
        val controller = new Controller(store)
        val server = HttpServer.create(new InetSocketAddress(port), 0)

        server.createContext("/", (exchange: HttpExchange) => {
            val req = Request(exchange.getRequestMethod, exchange.getRequestURI.toString)
            val res = controller.route(req)

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
