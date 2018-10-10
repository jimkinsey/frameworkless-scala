package todo

import Model.Item

object View
{
  def itemFragment(item: Item, number: Int): String = {
    s"""<li>
      <input type="checkbox" id="todo-$number"${if (item.done) " checked" else ""} name="${item.id}" onchange="this.form.submit();">
      <label for="todo-$number">${EscapeHTML.forElement(item.name)}</label>
      <button aria-label="delete ${EscapeHTML.forAttribute(item.name)}" name="delete" value="${item.id}">&times;</button>
    </li>"""
  }

  def page(items: Seq[Item] = Seq.empty, status: String = "", alert: String = ""): String = {
    s"""<!DOCTYPE HTML>
        <html>
          <head>
            <title>My Todo List</title>
            <link rel="stylesheet" type="text/css" href="/static/todo-mvp.css"/>
            <script src="/static/todo-mvp.js"></script>
          </head>
          <body>
           <section aria-labelledby="todos-label">
             <h1 id="todos-label">My Todo List</h1>
             <form action="/" method="POST">
               <ul>${items.zipWithIndex.map{ case (item, i) => itemFragment(item, i) }.mkString}</ul>
               <div class="empty-state">
                 <p>Either you've done everything already or there are still things to add to your list. Add your first todo &#x2193;</p>
               </div>
               <noscript><button type="submit">Update</button></noscript>
             </form>
             <form name="add" action="/" method="POST">
               <input type="text" aria-label="Write a new todo item" placeholder="E.g. Adopt an owl" name="name">
               <button type="submit">Add</button>
             </form>
             <div role="status" aria-live="polite" class="vh">$status</div>
             <div role="alert" aria-live="assertive">$alert</div>
           </section>
           <template id="item-template">
             ${itemFragment(ZeroItem, 0)}
           </template>
           <script>init();</script>
          </body>
        </html>"""
  }

  val ZeroItem = Item("", "", false)
}
