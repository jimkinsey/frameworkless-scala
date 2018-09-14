package todo

import java.net.{HttpURLConnection, URL}

class AcceptanceTests
{
    def test404ForNoSuchTodo(): Unit = {
      val server = Todo.start(9090)
      val res = HTTP.get("http://localhost:9090/todos/doesnt-exist")
      assert(res.status == 404)
      server.stop(0)
    }
}
