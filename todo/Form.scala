package todo

import java.net.URLDecoder.decode
import java.net.{URLDecoder, URLEncoder}

object Form
{
  type Values = Map[String, Seq[String]]

  def body(form: Values): String = {
    form.foldLeft[Seq[String]](Seq.empty) {
      case (acc, (name, values)) => acc ++ values.map(v => s"$name=${URLEncoder.encode(v, "UTF-8")}")
    } mkString "&"
  }

  def values(body: String): Values = {
    def merge(v1: Values, v2: Values): Values = (v1.keySet ++ v2.keySet) map {
      k => k -> (v1.getOrElse(k, Seq.empty) ++ v2.getOrElse(k, Seq.empty))
    } toMap

    """([^&]+?)=([^&]*)""".r.findFirstMatchIn(body) match {
      case Some(m) =>
        merge(Map(decode(m.group(1), "UTF-8") -> Seq(decode(m.group(2), "UTF-8"))), values(m.after.toString))
      case _ =>
        Map.empty
    }
  }
}
