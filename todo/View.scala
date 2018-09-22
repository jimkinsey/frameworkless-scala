package todo

object View
{
  def page: String = {
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
            </style>
          </head>
          <body>
           <section aria-labelledby="todos-label">
             <h1 id="todos-label">My Todo List</h1>
             <div class="empty-state">
               <p>Either you've done everything already or there are still things to add to your list. Add your first todo &#x2193;</p>
             </div>
           </section>
          </body>
        </html>"""
  }
}
