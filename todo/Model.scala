package todo

object Model
{
  case class Item(id: String, name: String, done: Boolean = false)
}
