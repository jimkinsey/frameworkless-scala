package todo

import todo.HTTP.Request

class ControllerTests
{
  def testPOSTWithValueOnForKnownItem(): Unit = {
    val todos = new Todos(new Store())
    val item = todos.add("Put out cat")
    val req = Request("POST", "/", Form.body(Map(item.id -> Seq("on"))))

    new Controller(todos).route(req)

    assert(todos.get(item.id) exists (_.done))
  }

  def testPOSTWithNameValue(): Unit = {
    val todos = new Todos(new Store())
    val req = Request("POST", "/", Form.body(Map("name" -> Seq("Put out cat"))))

    new Controller(todos).route(req)

    assert(todos.list exists (_.name == "Put out cat"))
  }

  def testPOSTWithNoOnValueForCompletedItem(): Unit = {
    val todos = new Todos(new Store())
    val item = todos.add("Bring in cat")
    todos.checkOff(item.id)
    val req = Request("POST", "/", Form.body(Map()))

    new Controller(todos).route(req)

    assert(todos.get(item.id) exists (_.done == false))
  }

  def testPOSTForMultipleItemsWithDifferentStates(): Unit = {
    val todos = new Todos(new Store())

    val batheCat = todos.add("Bathe cat")
    todos.checkOff(batheCat.id)
    val shootDog = todos.add("Shoot dog")

    val req = Request("POST", "/", Form.body(Map(shootDog.id -> Seq("on"))))

    new Controller(todos).route(req)

    assert(
      todos.get(batheCat.id).exists(_.done == false) && todos.get(shootDog.id).exists(_.done)
    )
  }
}
