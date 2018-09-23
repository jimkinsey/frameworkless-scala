package todo

object View
{
  def page(items: Seq[String] = Seq.empty): String = {
    s"""<!DOCTYPE HTML>
        <html>
          <head>
            <title>My Todo List</title>
            <style>
              .empty-state, ul:empty {
                display: none;
              }
              ul:empty + .empty-state {
                display: block;
              }

              ::-webkit-input-placeholder {
                color: #444;
                font-style: italic;
              }
              ::-moz-placeholder {
                color: #444;
                font-style: italic;
              }
              :-ms-input-placeholder {
                color: #444;
                font-style: italic;
              }
              :-moz-placeholder {
                color: #444;
                font-style: italic;
              }
            </style>
          </head>
          <body>
           <section aria-labelledby="todos-label">
             <h1 id="todos-label">My Todo List</h1>
             <ul>${items.map(i => s"<li>$i</li>").mkString}</ul>
             <div class="empty-state">
               <p>Either you've done everything already or there are still things to add to your list. Add your first todo &#x2193;</p>
             </div>
             <form action="/" method="POST">
               <input type="text" aria-label="Write a new todo item" placeholder="E.g. Adopt an owl" name="name">
               <button type="submit">Add</button>
             </form>
           </section>
          </body>
        </html>"""
  }
}
