package todo

import scala.util.Try

object TestRunner
extends App
{
  println("Starting test run...")
  
  FindClasses().filter(_.getName.endsWith("Tests")).foreach { suite: Class[_] =>
    println(suite.getName)
    val tests = suite.getMethods.toSeq.filter(_.getName.startsWith("test"))
    lazy val s = suite.newInstance()
    tests.foreach { t =>
      Try(suite.getMethod("setUp")).foreach(_.invoke(s))
      try {
        t.invoke(s)
        println(s"+ ${t.getName}")
      } catch {
        case f: Throwable =>
          println(s"- ${t.getName}")
          throw f
      } finally {
        Try(suite.getMethod("tearDown")).foreach(_.invoke(s))
      }
    }
  }

  println("DONE")
}

