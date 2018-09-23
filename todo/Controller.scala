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
      case ("POST", "/") =>
        val form = Form.values(req.body)
        form.get("name") match {
          case Some(name :: Nil) =>
            store.put(Item(randomUUID.toString, name))
            Response(
              status = 201,
              body = View.page(store.getAll, feedback = s"$name added."),
              headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
            )
          case _ =>
            val updated = form.collect {
              case (id, values) if values.headOption.contains("on") => id
            } flatMap store.get
            updated.foreach { item =>
              store.put(item.copy(done = true))
            }
            Response(
              status = 200,
              body = View.page(store.getAll, feedback = s"TODO"),
              headers = Map("Content-Type" -> Seq("text/html; charset=UTF-8"))
            )
        }
      case _ =>
        Response(404)
    }
}
