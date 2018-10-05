package todo

import cats.effect.IO
import org.http4s.Charset.`UTF-8`
import org.http4s.MediaType.`text/css`
import org.http4s.Method.GET
import org.http4s.Uri.Path
import org.http4s.client.blaze.Http1Client
import org.http4s.dsl.io._
import org.http4s.headers.`Content-Type`
import org.http4s.{Cookie, EntityDecoder, Method, Request, Uri, UrlForm}
import todo.testing.pages
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
        res.contentType contains `Content-Type`(`text/css`, `UTF-8`),
        css.nonEmpty,
      )
    }

  }

  def fetch(
    path: Path = "/",
    method: Method = GET,
    cookies: Map[String, String] = Map.empty,
    form: Map[String, String] = Map.empty)(implicit port: Int) = {

    lazy val testAppUri = Uri.unsafeFromString(s"http://$testAppHost:$port")

    val ioToString = implicitly[EntityDecoder[IO, String]]

    val request = Request[IO](method = method, uri = testAppUri withPath path).withBody(UrlForm(form.mapValues(v => Seq(v)))).unsafeRunSync()
    val requestWithCookies = cookies.foldLeft(request) {
      case (acc, (name, value)) => acc.addCookie(Cookie(name, value))
    }

    client.fetch(requestWithCookies)(resp =>
      IO(resp, ioToString.decode(resp, strict = false).fold(throw _, identity).unsafeRunSync)
    ).unsafeRunSync
  }

  def startApplication(port: Int)  = {
    App.bootstrap(testAppHost, port).compile.drain.unsafeToFuture
  }

  lazy val client = Http1Client[IO]().unsafeRunSync
  lazy val testAppHost = "localhost"
}
