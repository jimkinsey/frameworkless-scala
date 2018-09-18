package todo

class Store
{
    def get(id: String): Option[String] = storage.get(id)
    def put(id: String) = storage.put(id, id)
    def delete(id: String) = storage.remove(id)

    def getAll: Seq[String] = storage.keySet.toSeq.sorted

    private var storage: scala.collection.mutable.Map[String, String] = scala.collection.mutable.Map()
}
