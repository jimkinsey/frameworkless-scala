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
    assert("""Get milk""".r.findFirstMatchIn(res.body).isDefined)
  }

  def testAddingAnItemUpdatesTheLiveFeedbackRegion(): Unit = {
    val res = HTTP.post("http://localhost:9090", Form.body(Map("name" -> Seq("Get milk"))))
    assert("""<div.+role="status".*>.*Get milk added.""".r.findFirstMatchIn(res.body).isDefined)
  }

  def testDeletingAnItem(): Unit = {
    val create = HTTP.post("http://localhost:9090", Form.body(Map("name" -> Seq("Get milk"))))
    val getMilkID = """<input.+name="([a-f0-9\-]+)"""".r.findFirstMatchIn(create.body).get.group(1)
    val res = HTTP.post("http://localhost:9090", Form.body(Map("delete" -> Seq(getMilkID))))

    assert("""<div.+role="status".*>.*Get milk deleted.""".r.findFirstMatchIn(res.body).isDefined)
    assert(!res.body.contains(getMilkID))
  }

  def testCheckingOffAnItem(): Unit = {
    val create = HTTP.post("http://localhost:9090", Form.body(Map("name" -> Seq("Get milk"))))
    val getMilkID = """<input.+name="([a-f0-9\-]+)"""".r.findFirstMatchIn(create.body).get.group(1)
    val res = HTTP.post("http://localhost:9090", Form.body(Map(getMilkID -> Seq("on"))))

    assert("""<div.+role="status".*>.*Get milk checked off.""".r.findFirstMatchIn(res.body).isDefined)
    assert(s"""<input.+name="$getMilkID"[^>]+?>""".r.findFirstMatchIn(res.body) exists (_.matched.contains("checked")))
  }

  def testUncheckingAnItem(): Unit = {
    val create = HTTP.post("http://localhost:9090", Form.body(Map("name" -> Seq("Get milk"))))
    val getMilkID = """<input.+name="([a-f0-9\-]+)"""".r.findFirstMatchIn(create.body).get.group(1)
    HTTP.post("http://localhost:9090", Form.body(Map(getMilkID -> Seq("on"))))
    val res = HTTP.post("http://localhost:9090", Form.body(Map()))

    assert("""<div.+role="status".*>.*Get milk unchecked.""".r.findFirstMatchIn(res.body).isDefined)
    assert(!(s"""<input.+name="$getMilkID"[^>]+?>""".r.findFirstMatchIn(res.body) exists (_.matched.contains("checked"))))
  }

  def tearDown() = {
    server.stop(0)
  }
}
