package todo

import cats.effect.IO
import org.http4s.{Charset, MediaType, Request, Response, UrlForm}
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`

class Controller
(
  todos: Todos,
)
{
  type Action = PartialFunction[Request[IO], IO[Response[IO]]]

  def page: Action  = {
    case GET -> Root =>
      Ok(View.page(todos.list)).withContentType(`Content-Type`(MediaType.`text/html`, Charset.`UTF-8`))
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
            .withContentType(`Content-Type`(MediaType.`text/html`, Charset.`UTF-8`))
        case form if form.getFirst("name").isDefined =>
          val name = form.getFirst("name").head
          todos.add(name)

          Created(View.page(todos.list, feedback = s"$name added."))
            .withContentType(`Content-Type`(MediaType.`text/html`, Charset.`UTF-8`))
        case form =>
          val (checkedOff, unchecked) = todos.checkOff(form.values.filter(_._2 contains "on").keys.toSeq:_*).partition(_.done)

          val prefix = if (checkedOff.nonEmpty) s"${ListDescription(checkedOff.map(_.name))} checked off" else ""
          val suffix = if (unchecked.nonEmpty) s"${ListDescription(unchecked.map(_.name))} unchecked" else ""
          val separator = if (checkedOff.nonEmpty && unchecked.nonEmpty) ", " else ""

          Ok(View.page(todos.list, feedback = s"$prefix$separator$suffix."))
            .withContentType(`Content-Type`(MediaType.`text/html`, Charset.`UTF-8`))
      }
  }
}
