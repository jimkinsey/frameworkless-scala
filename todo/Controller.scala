package todo

import todo.HTTP.{Request, Response}

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
        store.put(form("name").head)
        Response(201, View.page(store.getAll), Map("Content-Type" -> Seq("text/html; charset=UTF-8")))
      case _ =>
        Response(404)
    }
}
