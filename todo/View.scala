package todo

object View
{
  def page: String = {
    s"""<!DOCTYPE HTML>
        <html>
          <head>
            <title>My Todo List</title>
          </head>
          <body>
           <section aria-labelledby="todos-label">
             <h1 id="todos-label">My Todo List</h1>
           </section>
          </body>
        </html>"""
  }
}
