package todo

import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpServer}

object Todo
{
    def start(port: Int) = {
        val store = new TodoStore()
        val server = HttpServer.create(new InetSocketAddress(port), 0)
        server.createContext("/", (exchange: HttpExchange) => {
            exchange.getRequestMethod match {
                case "GET" => {
                    val id = exchange.getRequestURI.getPath.split("/").last
                    store.get(id) match {
                        case Some(t) => exchange.sendResponseHeaders(200, -1)
                        case None => exchange.sendResponseHeaders(404, -1)
                    }
                }
                case "PUT" =>
                    val id = exchange.getRequestURI.getPath.split("/").last
                    store.put(id)
                    exchange.sendResponseHeaders(201, -1)
                case _ =>
                    exchange.sendResponseHeaders(404, -1)
            }
        })
        server.setExecutor(null)
        server.start()
        server
    }
}

class TodoStore
{
    def get(id: String): Option[String] = storage.get(id)
    def put(id: String) = storage.put(id, id)

    private var storage: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map()
}

