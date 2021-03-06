package todo

import todo.HTTP.{Request, Response}
import java.nio.file.Files
import java.nio.file.Paths

class Controller
(
  todos: Todos,
)
{
  def route(req: Request): Response =
    (req.method, req.uri) match {

      case ("GET", Static(filename)) =>
        Response(
          status = 200,
          body = new String(Files.readAllBytes(Paths.get(s"todo/$filename"))),
          headers = Map("Content-Type" -> Seq("text/css; charset=UTF-8"))
        )

      case ("GET", "/") =>
        Response(200, View.page(todos.list), Map("Content-Type" -> Seq("text/html; charset=UTF-8")))

      case ("POST", "/") if Form.values(req.body).get("delete").isDefined =>
        val name = for {
          ids <- Form.values(req.body).get("delete")
          id <- ids.headOption
          item <- todos.remove(id)
        } yield item.name

        Response(
          status = 200,
          body = View.page(todos.list, status = name.fold("")(n => s"$n deleted.")),
          headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
        )

      case ("POST", "/") if Form.values(req.body).get("name").isDefined =>
        val name = Form.values(req.body)("name").head
        todos.add(name)

        Response(
          status = 201,
          body = View.page(todos.list, status = s"$name added."),
          headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
        )

      case ("POST", "/") =>
        val form = Form.values(req.body)

        val (checkedOff, unchecked) = todos.checkOff(form.filter(_._2 contains "on").keys.toSeq:_*).partition(_.done)

        val prefix = if (checkedOff.nonEmpty) s"${ListDescription(checkedOff.map(_.name))} checked off" else ""
        val suffix = if (unchecked.nonEmpty) s"${ListDescription(unchecked.map(_.name))} unchecked" else ""
        val separator = if (checkedOff.nonEmpty && unchecked.nonEmpty) ", " else ""

        Response(
          status = 200,
          body = View.page(todos.list, status = s"$prefix$separator$suffix."),
          headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
        )

      case ("POST", "/todos") =>
        val item = todos.add(req.body)

        Response(
          status = 201,
          body = item.id
        )

      case ("PUT", Done(id)) =>
        val item = todos.update(id, req.body == "true")

        Response(status = 200)

      case ("GET", Done(id)) =>
        todos.get(id) match {
          case Some(item) =>
            Response(
              status = 200,
              body = item.done.toString
            )
          case None =>
            Response(404)
        }

      case ("DELETE", Todo(id)) =>
        todos.remove(id)

        Response(status = 204)

      case _ =>
        Response(404)
    }

  val Done = """\/todos\/(.+)\/done""".r
  val Todo = """\/todos\/(.+)""".r
  val Static = """\/static\/(.+?\.(?:css|js))""".r
}
