package todo

import java.util.UUID.randomUUID

import todo.Model.Item

class Todos
(
  store: Store,
)
{
  def get(id: String): Option[Item] = store.get(id)

  def list = store.getAll

  def remove(id: String) = {
    store.delete(id)
  }

  def add(name: String): Item = {
    val item = Item(randomUUID.toString, name)
    store.put(item)
    item
  }

  def checkOff(checkedOff: String*): Seq[Item] = {
    val updated = store.getAll.collect {
      case item if checkedOff.contains(item.id) && !item.done =>
        item.copy(done = true)
      case item if item.done =>
        item.copy(done = false)
    }

    updated.foreach(store.put)

    updated
  }

  def update(id: String, done: Boolean): Option[Item] = {
    get(id).map { old =>
      val updated = old.copy(done = done)
      store.put(updated)
      updated
    }
  }
}
