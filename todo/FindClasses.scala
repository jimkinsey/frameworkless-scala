package todo

import java.io.File

object FindClasses
{
    import scala.collection.JavaConverters._

    def apply(): Seq[Class[_]] = {
        val classLoader = Thread.currentThread.getContextClassLoader
        val resources = classLoader.getResources(".")
        val dirs = resources.asScala.map(_.getFile).toSeq
        dirs.filterNot(_.startsWith("bin"))
          .map(path => new File(path))
          .filter(_.isDirectory)
          .flatMap(findClasses(_, ""))
    }

    private def findClasses(dir: File, pkg: String): Seq[Class[_]] = {
        dir.listFiles.toSeq.flatMap {
          case f if f.isDirectory =>
            findClasses(f, f.getName)
          case f if f.getName.endsWith(".class") =>
            Seq(Class.forName(pkg + '.' + f.getName.dropRight(6)))
          case _ =>
            Seq.empty
        }
    }
}
