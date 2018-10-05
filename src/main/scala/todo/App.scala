package todo

import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.HttpService
import org.http4s.client.blaze.Http1Client
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware.{AutoSlash, GZip}

import scala.concurrent.ExecutionContext.Implicits.global

object App
extends StreamApp[IO]
{
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    bootstrap(host = "localhost", port = sys.env.get("PORT").map(_.toInt).getOrElse(8080))

  def bootstrap(host: String, port: Int): Stream[IO, ExitCode] =
    for {
      client <- Http1Client.stream[IO]()
      store = new Store()
      todos = new Todos(store)
      controller = new Controller(todos)
      routes = HttpService[IO] {
        controller.page orElse controller.submit orElse controller.stylesheet
      }
      exitCode <- startWeb(routes, host, port)
    } yield exitCode

  def startWeb(service: HttpService[IO], host: String, port: Int): Stream[IO, ExitCode] = {
    BlazeBuilder[IO]
      .bindHttp(port, host)
      .mountService(GZip(AutoSlash(service)), "/")
      .serve
  }
}
