package todo

class TodosTests
{
  def testAddCreatesANewItemInTheStore(): Unit = {
    val todos = new Todos(new Store())

    todos.add("Get milk")

    assert(todos.list exists (_.name == "Get milk"))
  }

  def testCheckOffMarksAnItemAsDone(): Unit = {
    val todos = new Todos(new Store())
    val getMilk = todos.add("Get milk")

    todos.checkOff(getMilk.id)

    assert(todos.get(getMilk.id) exists (_.done))
  }

  def testCheckOffMarksADoneItemAsUndone(): Unit = {
    val todos = new Todos(new Store())
    val getMilk = todos.add("Get milk")
    val putOutCat = todos.add("PutOutCat")

    todos.checkOff(putOutCat.id)
    todos.checkOff(getMilk.id)

    assert(todos.get(putOutCat.id) exists (_.done == false))
  }

  def testCheckOffReturnsItemsThatWereUpdated() {
    val todos = new Todos(new Store())
    val getMilk = todos.add("Get milk")

    val updated = todos.checkOff(getMilk.id)

    assert(updated.map(_.name) == Seq("Get milk"))
  }

  def testCheckOffDoesNotReturnItemsThatWereNotUpdated(): Unit = {
    val todos = new Todos(new Store())
    val getMilk = todos.add("Get milk")
    todos.add("Put out cat")

    val updated = todos.checkOff(getMilk.id)

    assert(updated.map(_.name) == Seq("Get milk"))
  }
}
