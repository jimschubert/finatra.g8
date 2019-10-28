package $package$.util.oplog

import cats.Show
import cats.syntax.apply._
import cats.effect.Sync
import $package$.util.PipeOperator._
import io.chrisdavenport.log4cats.{Logger, SelfAwareStructuredLogger}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

trait OpLogger {
  implicit private def unsafeLogger[F[_]: Sync]: SelfAwareStructuredLogger[F] = Slf4jLogger.getLogger[F]

  def logWithNoError[F[_]: Sync, D](l: NoErrorOperationLog[D])(implicit ds: Show[D]): F[String] =
    l.toJsonString.|> { s =>
      Logger[F].info(s) *> Sync[F].delay(s)
    }

  def logWithError[F[_]: Sync, E, D](l: WithErrorOperationLog[E, D])(implicit ds: Show[D], es: Show[E]): F[String] =
    l.toJsonString.|> { s =>
      Logger[F].info(s) *> Sync[F].delay(s)
    }
}
