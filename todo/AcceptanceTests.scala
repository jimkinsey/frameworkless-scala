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

    def test200ForExistingTodo(): Unit = {
        val server = Todo.start(9090)
        HTTP.put("http://localhost:9090/todos/exists")
        val res = HTTP.get("http://localhost:9090/todos/exists")
        assert(res.status == 200)
        server.stop(0)
    }

    def test201ForCreatingTodo(): Unit = {
        val server = Todo.start(9090)
        val res = HTTP.put("http://localhost:9090/todos/new")
        assert(res.status == 201)
        server.stop(0)
    }
}
