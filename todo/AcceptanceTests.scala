package todo

import java.net.{HttpURLConnection, URL}

import com.sun.net.httpserver.HttpServer

class AcceptanceTests
{
    var server: HttpServer = _

    def setUp() = {
        server = Todo.start(9090)
    }

    def test404ForNoSuchTodo(): Unit = {
        val res = HTTP.get("http://localhost:9090/todos/doesnt-exist")
        assert(res.status == 404)
    }

    def test200ForExistingTodo(): Unit = {
        HTTP.put("http://localhost:9090/todos/exists")
        val res = HTTP.get("http://localhost:9090/todos/exists")
        assert(res.status == 200)
    }

    def test201ForCreatingTodo(): Unit = {
        val res = HTTP.put("http://localhost:9090/todos/new")
        assert(res.status == 201)
    }

    def test204ForDeletingTodo(): Unit = {
        HTTP.put("http://localhost:9090/todos/exists")
        val res = HTTP.delete("http://localhost:9090/todos/exists")
        assert(res.status == 204)
    }

    def tearDown() = {
        server.stop(0)
    }
}
