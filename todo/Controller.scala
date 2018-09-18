package todo

import todo.HTTP.{Request, Response}

class Controller
(
  store: Store,
)
{
    def route(req: Request): Response = {
        (req.method, req.uri) match {
            case ("GET", Todos(id)) =>
              store.get(id) match {
                  case Some(t) => Response(200)
                  case None => Response(404)
              }
            case ("PUT", Todos(id)) =>
                store.put(id)
                Response(201)
            case ("DELETE", Todos(id)) =>
                store.delete(id)
                Response(204)
            case ("GET", TodoList) =>
                Response(200, store.getAll.mkString("\n"))
            case _ =>
                Response(404)
        }
    }

    val Todos = """\/todos\/([\w\d-]+)""".r
    val TodoList = "/todos"
}