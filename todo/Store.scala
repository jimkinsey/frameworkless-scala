package todo

class Store
{
  def get(id: String): Option[String] = storage.get(id)
  def put(id: String): Option[String] = storage.put(id, id)
  def delete(id: String): Option[String] = storage.remove(id)

  def getAll: Seq[String] = storage.keySet.toSeq.sorted

  private val storage: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map()
}
