package todo

import cats.effect.IO
import org.http4s.Charset.`UTF-8`
import org.http4s.MediaType.`text/css`
import org.http4s.Method.GET
import org.http4s.Uri.Path
import org.http4s.client.blaze.Http1Client
import org.http4s.headers.`Content-Type`
import org.http4s.dsl.io._
import org.http4s.{Charset, Cookie, EntityDecoder, MediaType, Method, Request, Uri, UrlForm}
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

      // TODO JSOUP!!!
      assert("""<div.+role="status".*>.*Get milk added.""".r.findFirstMatchIn(body).isDefined)
    }

    "Deleting an item" - {
      implicit val port = findFreePort
      startApplication(port)

      val (_, create) = fetch(method = POST, form = Map("name" -> "Get milk"))
      val getMilkID = """<input.+name="([a-f0-9\-]+)"""".r.findFirstMatchIn(create).get.group(1)
      val (_, body) = fetch(method = POST, form = Map("delete" -> getMilkID))

      assert(
        """<div.+role="status".*>.*Get milk deleted.""".r.findFirstMatchIn(body).isDefined,
        !body.contains(getMilkID)
      )
    }

    "Checking off an item" - {
      implicit val port = findFreePort
      startApplication(port)

      val (_, create) = fetch(method = POST, form = Map("name" -> "Get milk"))
      val getMilkID = """<input.+name="([a-f0-9\-]+)"""".r.findFirstMatchIn(create).get.group(1)
      val (_, body) = fetch(method = POST, form = Map(getMilkID -> "on"))

      assert("""<div.+role="status".*>.*Get milk checked off.""".r.findFirstMatchIn(body).isDefined)
      assert(s"""<input.+name="$getMilkID"[^>]+?>""".r.findFirstMatchIn(body) exists (_.matched.contains("checked")))
    }

    "Unchecking an item" - {
      implicit val port = findFreePort
      startApplication(port)

      val (_, create) = fetch(method = POST, form = Map("name" -> "Get milk"))
      val getMilkID = """<input.+name="([a-f0-9\-]+)"""".r.findFirstMatchIn(create).get.group(1)
      fetch(method = POST, form = Map(getMilkID -> "on"))
      val (_, body) = fetch(method = POST, form = Map())

      assert("""<div.+role="status".*>.*Get milk unchecked.""".r.findFirstMatchIn(body).isDefined)
      assert(!(s"""<input.+name="$getMilkID"[^>]+?>""".r.findFirstMatchIn(body) exists (_.matched.contains("checked"))))
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
