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
    val updated = store.getAll.map {
      case item if checkedOff contains item.id =>
        item.copy(done = true)
      case item =>
        item.copy(done = false)
    }

    updated.foreach(store.put)

    updated
  }
}
