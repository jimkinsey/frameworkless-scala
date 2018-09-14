package todo

object App
{
  def main(args: Array[String]) {
    Todo.start(args(0).toInt)
  }
}