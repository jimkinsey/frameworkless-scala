package todo

import java.util.UUID.randomUUID

import todo.HTTP.{Request, Response}
import todo.Model.Item

class Controller
(
  store: Store,
)
{
  def route(req: Request): Response =
    (req.method, req.uri) match {
      case ("GET", "/") =>
        Response(200, View.page(store.getAll), Map("Content-Type" -> Seq("text/html; charset=UTF-8")))
      case ("POST", "/") if Form.values(req.body).get("delete").isDefined =>
        val name = for {
          ids <- Form.values(req.body).get("delete")
          id <- ids.headOption
          item <- store.delete(id)
        } yield {
          item.name
        }

        Response(
          status = 200,
          body = View.page(store.getAll, feedback = name.fold("")(n => s"$n deleted.")),
          headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
        )
      case ("POST", "/") if Form.values(req.body).get("name").isDefined =>
        val name = Form.values(req.body)("name").head
        store.put(Item(randomUUID.toString, name))

        Response(
          status = 201,
          body = View.page(store.getAll, feedback = s"$name added."),
          headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
        )
      case ("POST", "/") =>
        val form = Form.values(req.body)

        val updated = store.getAll.map {
          case item if form.get(item.id) contains List("on") =>
            item.copy(done = true)
          case item =>
            item.copy(done = false)
        }

        updated.foreach(store.put)

        val checkedOff = updated.filter(_.done)

        Response(
          status = 200,
          body = View.page(store.getAll, feedback = s"${ListDescription(checkedOff.map(_.name))} checked off."),
          headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
        )
      case _ =>
        Response(404)
    }
}
