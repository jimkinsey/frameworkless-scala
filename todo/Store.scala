package todo

import todo.Model.Item

import scala.collection.mutable

class Store
{
  def get(id: String): Option[Item] = storage.find(_.id == id)

  def put(item: Item): Unit = {
    delete(item.id)
    storage.append(item)
  }

  def delete(id: String): Option[Item] = get(id).map(storage.indexOf).map(storage.remove)

  def getAll: Seq[Item] = storage

  private val storage: mutable.Buffer[Item] = mutable.Buffer()
}
