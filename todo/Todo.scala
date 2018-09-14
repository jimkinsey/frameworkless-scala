package todo

import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpServer}

object Todo
{
    def start(port: Int) = {
        val server = HttpServer.create(new InetSocketAddress(port), 0)
        server.createContext("/", (exchange: HttpExchange) => {
            exchange.sendResponseHeaders(404, -1)
        })
        server.setExecutor(null)
        server.start()
        server
    }


}
