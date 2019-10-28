package $package$.http.retry

import cats.effect.{IO, Timer}
import cats.instances.option._
import cats.syntax.apply._
import cats.syntax.semigroup._
import $package$.util.AppConfigLib._
import com.twitter.util.logging.Logging
import monix.execution.Scheduler
import perfolation._
import retry.CatsEffect._
import retry.RetryDetails.{GivingUp, WillDelayAndRetry}
import retry.RetryPolicies._
import retry.{RetryPolicy, _}

import scala.concurrent.duration._

trait HttpClientRetry extends Logging {
  final case class RetryConfig(retryTimes: Int, delay: Long)

  private def logError(err: Throwable, details: RetryDetails): IO[Unit] = details match {

    case WillDelayAndRetry(nextDelay: FiniteDuration, retriesSoFar: Int, cumulativeDelay: FiniteDuration) =>
      IO(
        error(
          p"Http client request failed. So far we have retried $"$"$retriesSoFar times.\nException: [$"$"${err.getMessage}]"
        )
      )

    case GivingUp(totalRetries: Int, totalDelay: FiniteDuration) =>
      IO(error(p"Http client request: Giving up after $"$"$totalRetries retries.\nException: [$"$"${err.getMessage}]"))

  }

  implicit private val timer: Timer[IO] = IO.timer(Scheduler.io("http-retry-cs"))

  private val config: IO[RetryConfig] = IO(
    (getConfig[Int]("RETRY_TIMES"), getConfig[Long]("RETRY_DELAY"))
      .mapN((retryTimes, retryDelay) => RetryConfig(retryTimes, retryDelay))
      .getOrElse(RetryConfig(3, 50))
  )

  private val policy: IO[RetryPolicy[IO]] =
    config.map(c => limitRetries[IO](c.retryTimes) |+| constantDelay[IO](c.delay.milliseconds))

  def withRetry[A](action: => IO[A]): IO[A] =
    policy.flatMap(p => retryingOnAllErrors[A](policy = p, onError = logError)(action))
}
