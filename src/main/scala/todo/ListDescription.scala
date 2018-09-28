package todo

object ListDescription
{
  def apply(strings: Seq[String]): String = {
    strings.zipWithIndex.foldLeft("") {
      case (acc, (str, i)) if acc.nonEmpty && i == strings.length - 1 => s"$acc and $str"
      case (acc, (str, i)) if acc.nonEmpty => s"$acc, $str"
      case (_, (str, _)) => str
    }
  }
}
