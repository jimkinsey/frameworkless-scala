package todo

import cats.effect.Sync
import cats.syntax.flatMap._
import pureconfig.error.ConfigReaderException
import pureconfig.loadConfig

case class Config(port: Int)

object Config
{
  def load[F[_]](implicit E: Sync[F]): F[Config] =
    E.delay(loadConfig[Config]).flatMap {
      case Right(ok) => E.pure(ok)
      case Left(e) => E.raiseError(new ConfigReaderException[Config](e))
    }
}
