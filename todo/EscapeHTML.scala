package todo

object EscapeHTML
{
  def forElement(str: String): String = str
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#x27;")
    .replaceAll("\\/", "&#x2F;")

  def forAttribute(str: String): String = str.map {
    case c if c.toInt < 256 && !c.isLetterOrDigit && !c.isSpaceChar => s"&#x${c.toInt.toHexString};"
    case c => c
  }.mkString
}
