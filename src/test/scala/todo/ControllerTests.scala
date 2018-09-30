package todo

import cats.effect.IO
import org.http4s.Method.POST
import org.http4s.dsl.io._
import org.http4s.{Request, Uri, UrlForm}
import utest._

object ControllerTests
extends TestSuite
{
  val tests = Tests {

    "POST with value on for known item" - {
      val todos = new Todos(new Store())
      val item = todos.add("Put out cat")
      val req = Request[IO](POST, Uri(path = "/")).withBody(UrlForm(item.id -> "on")).unsafeRunSync()

      new Controller(todos).submit(req).unsafeRunSync()

      assert(todos.get(item.id) exists (_.done))
    }

    "POST with name to create new item" - {
      val todos = new Todos(new Store())
      val req = Request[IO](POST).withBody(UrlForm("name" -> "Put out cat")).unsafeRunSync()

      new Controller(todos).submit(req).unsafeRunSync()

      assert(todos.list exists (_.name == "Put out cat"))
    }

    "POST with no value on for done item" - {
      val todos = new Todos(new Store())
      val item = todos.add("Bring in cat")
      todos.checkOff(item.id)
      val req = Request[IO](POST)

      new Controller(todos).submit(req).unsafeRunSync()

      assert(todos.get(item.id) exists (_.done == false))
    }

    "POST for multiple items in different states" - {
      val todos = new Todos(new Store())

      val batheCat = todos.add("Bathe cat")
      todos.checkOff(batheCat.id)
      val shootDog = todos.add("Shoot dog")
      val req = Request[IO](POST).withBody(UrlForm(shootDog.id -> "on")).unsafeRunSync()

      new Controller(todos).submit(req).unsafeRunSync()

      assert(
        todos.get(batheCat.id).exists(_.done == false) && todos.get(shootDog.id).exists(_.done)
      )
    }
  }
}
