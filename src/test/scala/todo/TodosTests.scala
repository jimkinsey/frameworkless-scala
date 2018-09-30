package todo

import utest._

object TodosTests
extends TestSuite
{
  val tests = Tests {

    "Add creates a new item" - {
      val todos = new Todos(new Store())

      todos.add("Get milk")

      assert(todos.list exists (_.name == "Get milk"))
    }

    "Check-off marks an item as done" - {
      val todos = new Todos(new Store())
      val getMilk = todos.add("Get milk")

      todos.checkOff(getMilk.id)

      assert(todos.get(getMilk.id) exists (_.done))
    }

    "Check-off marks a done item as undone" - {
      val todos = new Todos(new Store())
      val getMilk = todos.add("Get milk")
      val putOutCat = todos.add("PutOutCat")

      todos.checkOff(putOutCat.id)
      todos.checkOff(getMilk.id)

      assert(todos.get(putOutCat.id) exists (_.done == false))
    }

    "Check-off returns items that were updated" - {
      val todos = new Todos(new Store())
      val getMilk = todos.add("Get milk")

      val updated = todos.checkOff(getMilk.id)

      assert(updated.map(_.name) == Seq("Get milk"))
    }

    "Check-off does not return items that were not updated" - {
      val todos = new Todos(new Store())
      val getMilk = todos.add("Get milk")
      todos.add("Put out cat")

      val updated = todos.checkOff(getMilk.id)

      assert(updated.map(_.name) == Seq("Get milk"))
    }

  }
}