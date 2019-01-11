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
    bootstrap(host = "0.0.0.0")

  def bootstrap(host: String, port: Option[Int] = None): Stream[IO, ExitCode] =
    Try {
      for {
        _ <- Stream.eval(IO(println("Loading config...")))
        conf <- Stream.eval(Config.load[IO])
        _ = println(s"Loaded config $conf")
        _ = println("Intialising store...")
        store = new Store()
        _ = println("Initialising todos...")
        todos = new Todos(store)
        _ = println("Initialising controller...")
        controller = new Controller(todos)
        _ = println("Initialising routes...")
        routes = HttpService[IO] {
          controller.page orElse controller.api orElse controller.submit orElse controller.stylesheet
        }
        _ = println("Starting web-server...")
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
    println(s"Launching app on host [$host] port [$port]...")
    BlazeBuilder[IO]
      .bindHttp(port, host)
      .mountService(GZip(AutoSlash(service)), "/")
      .serve
  }
}
