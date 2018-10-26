package todo

import scalatags.Text
import todo.Model.Item
import scalatags.Text.all._

object View
{
  val section: Text.TypedTag[String] = tag("section")
  val noscript: Text.TypedTag[String] = tag("noscript")

  def page(items: Seq[Item] = Seq.empty, feedback: String = "", alert: String = ""): String = {

    "<!DOCTYPE HTML>\n" + html(
      head(
        tag("title")("My Todo List"),
        link(rel := "stylesheet", `type` := "text/css", href := "/static/todo-mvp.css"),
        script(src := "https://cdn.jsdelivr.net/npm/vue@2.5.17/dist/vue.js"),
      ),
      body(
        section(
          aria.labelledby := "todos-label",
          h1(id := "todos-label", "My Todo List"),
          form(action := "/", method := "POST")(
            ul(items.zipWithIndex.map { case (item, i) =>
              li(
                input(`type` := "checkbox", id := s"todo-$i", (if (item.done) Some(checked) else None).asInstanceOf[Option[Modifier]], name := item.id, onchange := "this.form.submit();"),
                label(`for` := s"todo-$i")(item.name),
                button(aria.label := s"delete ${item.name}", name := "delete", value := item.id)(raw("&times;"))
              )
            }),
            div(`class` := "empty-state")(
              p(raw("Either you've done everything already or there are still things to add to your list. Add your first todo &#x2193;"))
            ),
            noscript(button(`type` := "submit")("Update"))
          ),
          form(action := "/", method := "POST")(
            input(`type` := "text", aria.label := "Write a new todo item", placeholder := "E.g. Adopt an owl", name := "name"),
            button(`type` := "submit")("Add")
          ),
          div(role := "status", aria.live := "polite", `class` := "vh")(feedback),
          div(role := "alert", aria.live := "assertive")(alert),
        ),
        script(src := "/static/todo-mvp.js"),
      )
    )
  }
}
