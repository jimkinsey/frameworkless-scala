package todo.testing

import java.net.{ServerSocket, Socket}

import scala.io.Source
import scala.util.{Failure, Success, Try}

trait Resources
{
  def loadResource(resource: String): String = {
    Source.fromInputStream(getClass.getClassLoader.getResourceAsStream(resource)).mkString
  }

  def tryWithResources[A <: AutoCloseable, B](resource: A)(block: A => B): B = {
    Try(block(resource)) match {
      case Success(result) =>
        resource.close()
        result
      case Failure(e) =>
        resource.close()
        throw e
    }
  }

  def findFreePort: Int = {
    tryWithResources(new ServerSocket(0)) { socket =>
      socket.setReuseAddress(true)
      socket.getLocalPort
    }
  }

  def isServerListening(host: String, port: Int): Boolean = {
    try {
      tryWithResources(new Socket(host, port)) { s =>
        true
      }
    } catch {
      case e: Throwable => false
    }
  }
}
