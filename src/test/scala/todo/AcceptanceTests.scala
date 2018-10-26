package todo

import cats.effect.IO
import org.http4s.Charset.`UTF-8`
import org.http4s.MediaType.{`application/javascript`, `text/css`}
import org.http4s.Method.GET
import org.http4s.Uri.Path
import org.http4s.client.blaze.Http1Client
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`
import org.http4s.{Cookie, EntityDecoder, Method, Request, Uri, UrlForm}
import todo.testing.pages.TodoListPage
import todo.testing.{Resources, pages}
import utest._

object AcceptanceTests
extends TestSuite
with Resources
{
  val tests = Tests {

    "Adding an item" - {
      implicit val port = findFreePort
      startApplication(port)

      val (_, body) = fetch(method = POST, form = Map("name" -> "Get milk"))

      val page = pages.TodoListPage(body)

      assert(page.liveFeedback contains "Get milk added.")
    }

    "Deleting an item" - {
      implicit val port = findFreePort
      startApplication(port)

      val (_, createBody) = fetch(method = POST, form = Map("name" -> "Get milk"))
      val getMilkID = pages.TodoListPage(createBody).todoList.head.id
      val (_, body) = fetch(method = POST, form = Map("delete" -> getMilkID))

      val deletePage = pages.TodoListPage(body)

      assert(
        deletePage.liveFeedback contains "Get milk deleted.",
        !(deletePage.todoList exists (_.id == getMilkID)),
      )
    }

    "Checking off an item" - {
      implicit val port = findFreePort
      startApplication(port)

      val (_, createBody) = fetch(method = POST, form = Map("name" -> "Get milk"))
      val getMilkID = pages.TodoListPage(createBody).todoList.head.id
      val (_, body) = fetch(method = POST, form = Map(getMilkID -> "on"))

      val checkOffPage = pages.TodoListPage(body)

      assert(
        checkOffPage.liveFeedback contains "Get milk checked off.",
        checkOffPage.todoList.find(_.id == getMilkID) exists (_.checked),
      )
    }

    "Unchecking an item" - {
      implicit val port = findFreePort
      startApplication(port)

      val (_, createBody) = fetch(method = POST, form = Map("name" -> "Get milk"))
      val getMilkID = pages.TodoListPage(createBody).todoList.head.id

      fetch(method = POST, form = Map(getMilkID -> "on"))
      val (_, body) = fetch(method = POST, form = Map())

      val uncheckedPage = pages.TodoListPage(body)

      assert(
        uncheckedPage.liveFeedback contains "Get milk unchecked.",
        !(uncheckedPage.todoList.find(_.id == getMilkID) exists (_.checked)),
      )
    }

    "Serving the CSS file" - {
      implicit val port = findFreePort
      startApplication(port)

      val (res, css) = fetch(method = GET, path = "/static/todo-mvp.css")

      assert(
        res.status.code == 200,
        res.contentType contains `Content-Type`(`text/css`),
        css.nonEmpty,
      )
    }

    "Serving the JS file" - {
      implicit val port = findFreePort
      startApplication(port)

      val (res, js) = fetch(method = GET, path = "/static/todo-mvp.js")

      assert(
        res.status.code == 200,
        res.contentType contains `Content-Type`(`application/javascript`),
        js.nonEmpty,
      )
    }

    "Checking off via API" - {
      implicit val port = findFreePort
      startApplication(port)

      val (_, createBody) = fetch(method = POST, form = Map("name" -> "Get milk"))
      val getMilkID = pages.TodoListPage(createBody).todoList.head.id

      val (checkOffViaAPI, _) = fetch(path = s"/todos/$getMilkID/done", method = PUT, body = "true")
      val (_, body) = fetch()
      val page = TodoListPage(body)

      assert(
        checkOffViaAPI.status.code == 200,
        page.todoList.head.checked,
      )
    }

  }

  def fetch(
    path: Path = "/",
    method: Method = GET,
    cookies: Map[String, String] = Map.empty,
    form: Map[String, String] = Map.empty,
    body: String = "")(implicit port: Int) = {

    lazy val testAppUri = Uri.unsafeFromString(s"http://$testAppHost:$port")

    val ioToString = implicitly[EntityDecoder[IO, String]]

    val baseRequest = Request[IO](method = method, uri = testAppUri withPath path)
    val request = body match {
      case "" => baseRequest.withBody(UrlForm(form.mapValues(v => Seq(v)))).unsafeRunSync()
      case _ => baseRequest.withBody(body).unsafeRunSync()
    }

    val requestWithCookies = cookies.foldLeft(request) {
      case (acc, (name, value)) => acc.addCookie(Cookie(name, value))
    }

    client.fetch(requestWithCookies)(resp =>
      IO(resp, ioToString.decode(resp, strict = false).fold(throw _, identity).unsafeRunSync)
    ).unsafeRunSync
  }

  def startApplication(port: Int)  = {
    App.bootstrap(testAppHost, Some(port)).compile.drain.unsafeToFuture
  }

  lazy val client = Http1Client[IO]().unsafeRunSync
  lazy val testAppHost = "localhost"
}
