package todo

import java.net.InetSocketAddress

import com.sun.net.httpserver.{HttpExchange, HttpServer}

object Todo
{
    def start(port: Int) = {
        val store = new TodoStore()
        val server = HttpServer.create(new InetSocketAddress(port), 0)
        server.createContext("/", (exchange: HttpExchange) => {
            (exchange.getRequestMethod, exchange.getRequestURI.getPath) match {
                case ("GET", Todos(id)) => {
                    store.get(id) match {
                        case Some(t) => exchange.sendResponseHeaders(200, -1)
                        case None => exchange.sendResponseHeaders(404, -1)
                    }
                }
                case ("PUT", Todos(id)) =>
                    store.put(id)
                    exchange.sendResponseHeaders(201, -1)
                case ("DELETE", Todos(id)) =>
                    store.delete(id)
                    exchange.sendResponseHeaders(204, -1)
                case _ =>
                    exchange.sendResponseHeaders(404, -1)
            }
        })
        server.setExecutor(null)
        server.start()
        server
    }

    val Todos = """\/todos\/([\w\d]+)""".r
}

class TodoStore
{
    def get(id: String): Option[String] = storage.get(id)
    def put(id: String) = storage.put(id, id)
    def delete(id: String) = storage.remove(id)

    private var storage: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map()
}

