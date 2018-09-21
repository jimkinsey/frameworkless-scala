package todo

import com.sun.net.httpserver.HttpServer

object App
{
  def main(args: Array[String]) {
    val port = args.headOption.orElse(sys.env.get("PORT")).map(_.toInt).getOrElse(8080)
    launchServer(port)
    println(s"Listening on port $port")
  }

  def launchServer(port: Int): HttpServer = {
    val store = new Store()
    val controller = new Controller(store)
    val server = Server.start(port, controller.route)
    server
  }
}