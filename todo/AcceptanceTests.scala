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

  def testPageHasFormToCreateNewItems() {
    val res = HTTP.get("http://localhost:9090")
    assert("""<form.+action="/".+method="POST"""".r.findFirstMatchIn(res.body).isDefined)
  }

  def testSubmittingFormCreatesANewItem(): Unit = {
    val res = HTTP.post("http://localhost:9090", Form.body(Map("name" -> Seq("Get milk"))))
    assert("""<li.*>Get milk</li>""".r.findFirstMatchIn(res.body).isDefined)
  }

  def testAddingAnItemUpdatesTheLiveFeedbackRegion(): Unit = {
    val res = HTTP.post("http://localhost:9090", Form.body(Map("name" -> Seq("Get milk"))))
    assert("""<div.+role="status".*>.*Get milk added.""".r.findFirstMatchIn(res.body).isDefined)
  }

  def tearDown() = {
    server.stop(0)
  }
}
