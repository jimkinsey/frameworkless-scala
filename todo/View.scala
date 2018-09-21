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
            <h1>My Todo List</h1>
          </body>
        </html>"""
  }
}
