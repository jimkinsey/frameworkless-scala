package todo

import todo.Model.Item

object View
{
  def page(items: Seq[Item] = Seq.empty, feedback: String = ""): String = {
    s"""<!DOCTYPE HTML>
        <html>
          <head>
            <title>My Todo List</title>
            <link rel="stylesheet" type="text/css" href="/static/todo-mvp.css"/>
          </head>
          <body>
           <section aria-labelledby="todos-label">
             <h1 id="todos-label">My Todo List</h1>
             <form action="/" method="POST">
               <ul>${items.zipWithIndex.map { case (item, i) =>
                 s"""<li>
                   <input type="checkbox" id="todo-$i"${if (item.done) " checked" else ""} name="${item.id}" onchange="this.form.submit();">
                   <label for="todo-$i">${item.name}</label>
                   <button aria-label="delete ${item.name}" name="delete" value="${item.id}">&times;</button>
                 </li>"""
               }.mkString}</ul>
               <div class="empty-state">
                 <p>Either you've done everything already or there are still things to add to your list. Add your first todo &#x2193;</p>
               </div>
               <noscript><button type="submit">Update</button></noscript>
             </form>
             <form action="/" method="POST">
               <input type="text" aria-label="Write a new todo item" placeholder="E.g. Adopt an owl" name="name">
               <button type="submit">Add</button>
             </form>
             <div role="status" aria-live="polite" class="vh">$feedback</div>
           </section>
          </body>
        </html>"""
  }
}
