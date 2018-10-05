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
      case ("GET", "/static/todo-mvp.css") =>
        Response(
          status = 200,
          body = new String(Files.readAllBytes(Paths.get("todo/todo-mvp.css"))),
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
          body = View.page(todos.list, feedback = name.fold("")(n => s"$n deleted.")),
          headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
        )
      case ("POST", "/") if Form.values(req.body).get("name").isDefined =>
        val name = Form.values(req.body)("name").head
        todos.add(name)

        Response(
          status = 201,
          body = View.page(todos.list, feedback = s"$name added."),
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
          body = View.page(todos.list, feedback = s"$prefix$separator$suffix."),
          headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
        )
      case _ =>
        Response(404)
    }
}
