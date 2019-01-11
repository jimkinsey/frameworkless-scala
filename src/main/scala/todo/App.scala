package todo

import cats.effect.IO
import fs2.StreamApp.ExitCode
import fs2.{Stream, StreamApp}
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware.{AutoSlash, GZip}

import scala.util.{Try, Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

object App
extends StreamApp[IO]
{
  override def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, ExitCode] =
    bootstrap(host = "localhost")

  def bootstrap(host: String, port: Option[Int] = None): Stream[IO, ExitCode] =
    Try {
      for {
        conf <- Stream.eval(Config.load[IO])
        store = new Store()
        todos = new Todos(store)
        controller = new Controller(todos)
        routes = HttpService[IO] {
          controller.page orElse controller.api orElse controller.submit orElse controller.stylesheet
        }
        exitCode <- startWeb(
          service = routes,
          host = host,
          port = port.getOrElse(conf.port)
        )
      } yield exitCode
    } match {
      case Success(result) => result
      case Failure(f) => System.err.println(s"Failed to start the application - $f", f); Stream.empty
    }

  def startWeb(service: HttpService[IO], host: String, port: Int): Stream[IO, ExitCode] = {
    BlazeBuilder[IO]
      .bindHttp(port, host)
      .mountService(GZip(AutoSlash(service)), "/")
      .serve
  }
}
