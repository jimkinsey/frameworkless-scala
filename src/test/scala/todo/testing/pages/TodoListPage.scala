package todo.testing.pages

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.collection.JavaConverters._

object TodoListPage
{
  def apply(html: String): TodoListPage = {
    val dom = Jsoup.parse(html)
    new TodoListPage(
      liveFeedback = dom.select("div[role='status']").text(),
      todoList = dom.select("li input").asScala.toList.map(Item(_))
    )
  }

  case class Item(id: String, checked: Boolean)

  object Item
  {
    def apply(element: Element): Item = {
      Item(
        id = element.attr("name"),
        checked = element.is("input[checked]")
      )
    }
  }
}

case class TodoListPage(liveFeedback: String, todoList: List[TodoListPage.Item])