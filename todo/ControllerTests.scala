package todo

import todo.HTTP.Request
import todo.Model.Item

class ControllerTests
{
  def testPOSTWithValueOnForKnownItem(): Unit = {
    val store = new Store()
    store.put(Item("ID", "Put out cat"))
    val req = Request("POST", "/", Form.body(Map("ID" -> Seq("on"))))

    new Controller(store).route(req)

    assert(store.get("ID") exists (_.done))
  }

  def testPOSTWithNameValue(): Unit = {
    val store = new Store()
    val req = Request("POST", "/", Form.body(Map("name" -> Seq("Put out cat"))))

    new Controller(store).route(req)

    assert(store.getAll exists (_.name == "Put out cat"))
  }
}
