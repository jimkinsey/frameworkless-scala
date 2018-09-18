package todo

object App
{
  def main(args: Array[String]) {
    val port = args.headOption.orElse(sys.env.get("PORT")).map(_.toInt).getOrElse(8080)
    val server = Server.start(port)
    println(s"Listening on port ${server.getAddress.getPort}")
  }
}