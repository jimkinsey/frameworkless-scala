package todo

import com.sun.net.httpserver.HttpServer

class AcceptanceTests
{
  var server: HttpServer = _

  def setUp() = {
    server = App.launchServer(9090)
  }

  def testHTMLPageExists(): Unit = {
    val res = HTTP.get("http://localhost:9090/")
    assert(res.status == 200)
    assert(res.headers("Content-type") contains "text/html; charset=UTF-8")
  }

  def testPageHasHeading(): Unit = {
    val res = HTTP.get("http://localhost:9090/")
    assert("<h1.*>My Todo List</h1>".r.findFirstMatchIn(res.body).isDefined)
  }

  def testPageHasEmptyStateIndicator(): Unit = {
    val res = HTTP.get("http://localhost:9090")
    assert("""<div.+class="empty-state"""".r.findFirstMatchIn(res.body).isDefined)
  }

  def testNoSuchTodo(): Unit = {
    val res = HTTP.get("http://localhost:9090/todos/doesnt-exist")
    assert(res.status == 404)
  }

  def testGetTodo(): Unit = {
    HTTP.put("http://localhost:9090/todos/exists")
    val res = HTTP.get("http://localhost:9090/todos/exists")
    assert(res.status == 200)
  }

  def testCreatingTodo(): Unit = {
    val res = HTTP.put("http://localhost:9090/todos/new")
    assert(res.status == 201)
  }

  def testDeletingTodo(): Unit = {
    HTTP.put("http://localhost:9090/todos/exists")
    val res = HTTP.delete("http://localhost:9090/todos/exists")
    assert(res.status == 204)
  }

  def testListTodos() = {
    HTTP.put("http://localhost:9090/todos/get-milk")
    HTTP.put("http://localhost:9090/todos/hang-laundry")
    HTTP.put("http://localhost:9090/todos/put-out-cat")
    val res = HTTP.get("http://localhost:9090/todos")

    assert(res.body ==
      """get-milk
        |hang-laundry
        |put-out-cat""".stripMargin)
  }

  def tearDown() = {
    server.stop(0)
  }
}
