package todo

import cats.effect.IO
import org.http4s.Charset.`UTF-8`
import org.http4s.MediaType.{`text/css`, `text/html`}
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`
import org.http4s.{Request, Response, UrlForm, _}

class Controller
(
  todos: Todos,
)
{
  type Action = PartialFunction[Request[IO], IO[Response[IO]]]

  def stylesheet: Action = {
    case req @ GET -> Root / "static" / filename =>
      StaticFile.fromResource(s"/$filename", Some(req))
        .getOrElseF(NotFound())
  }

  def page: Action  = {
    case GET -> Root =>
      Ok(View.page(todos.list)).withContentType(`Content-Type`(`text/html`, `UTF-8`))
  }

  def submit: Action = {
    case req @ POST -> Root =>
      req.decode[UrlForm] {

        case form if form.getFirst("delete").isDefined =>
          val name = for {
            id <- form.getFirst("delete")
            item <- todos.remove(id)
          } yield item.name

          Ok(View.page(todos.list, feedback = name.fold("")(n => s"$n deleted.")))
            .withContentType(`Content-Type`(`text/html`, `UTF-8`))

        case form if form.getFirst("name").isDefined =>
          val name = form.getFirst("name").head
          todos.add(name)

          Created(View.page(todos.list, feedback = s"$name added."))
            .withContentType(`Content-Type`(`text/html`, `UTF-8`))

        case form =>
          val (checkedOff, unchecked) = todos.checkOff(form.values.filter(_._2 contains "on").keys.toSeq:_*).partition(_.done)

          val prefix = if (checkedOff.nonEmpty) s"${ListDescription(checkedOff.map(_.name))} checked off" else ""
          val suffix = if (unchecked.nonEmpty) s"${ListDescription(unchecked.map(_.name))} unchecked" else ""
          val separator = if (checkedOff.nonEmpty && unchecked.nonEmpty) ", " else ""

          Ok(View.page(todos.list, feedback = s"$prefix$separator$suffix."))
            .withContentType(`Content-Type`(`text/html`, `UTF-8`))

      }
  }

  def api: Action = {
    case req @ PUT -> Root / "todos" / id / "done" =>
      req.decode[String] { body =>
        todos.update(id, body.toBoolean)
        Ok()
      }
  }
}
