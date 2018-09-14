package todo

import java.io.File

import scala.util.{Try, Success, Failure}

object TestRunner
extends App
{
    println("Starting test run...")
    
    val suites = FindTestClasses()

    suites.foreach { s: Class[_] =>
        println(s.getName)
        lazy val i = s.newInstance()
        val tests = s.getMethods.toSeq.filter(_.getName.startsWith("test"))
        tests.foreach { t =>
            Try(t.invoke(i)) match {
                case Success(_) => println(s"+ ${t.getName}")
                case Failure(f) =>
                    println(s"- ${t.getName}")
                    throw f
            }
        }
    }

    println("DONE")
}

object FindTestClasses
{
    def apply() = getClasses("").filter(_.getName.endsWith("Tests"))

    import java.io.IOException
    import java.util

    import scala.collection.JavaConverters._

    private def getClasses(packageName: String) = {
        val classLoader = Thread.currentThread.getContextClassLoader
        val path = packageName.replace('.', '/')
        val resources = classLoader.getResources(".")
        val dirs = resources.asScala.map(_.getFile).toSeq
        dirs.filterNot(_.startsWith("bin")).flatMap { dir =>
          findClasses(new File(dir), packageName)
        }
    }

    private def findClasses(directory: File, packageName: String): Seq[Class[_]] = {
        if (!directory.exists()) return Seq.empty
        val files = directory.listFiles.toSeq

        files.flatMap { file =>
            if (file.isDirectory) {
                findClasses(file, file.getName)
            }
            else if (file.getName.endsWith(".class")) {
                Seq(Class.forName(packageName + '.' + file.getName.substring(0, file.getName.length - 6)))
            }
            else {
                Seq.empty
            }
        }
    }
}