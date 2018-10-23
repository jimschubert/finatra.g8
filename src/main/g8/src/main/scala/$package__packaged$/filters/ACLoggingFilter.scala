package $package$.filters

import $package$.util.PipeOperator._
import com.twitter.finagle.filter.LogFormatter
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.inject.Logging
import com.twitter.util.{Future, Return, Stopwatch, Throw}
import javax.inject.{Inject, Singleton}
import mouse.boolean._

@Singleton
class ACLoggingFilter[R <: Request] @Inject()(logFormatter: LogFormatter[R, Response])
    extends SimpleFilter[R, Response]
    with Logging {
  override def apply(request: R, service: Service[R, Response]): Future[Response] =
    (!isInfoEnabled).fold(
      service(request), {
        request.path.|>(
          p => {
            (p.contains("/version") || p.contains("/health")).fold(
              service(request), {
                val elapsed = Stopwatch.start()

                service(request).respond {
                  case Return(response) =>
                    info(logFormatter.format(request, response, elapsed()))
                  case Throw(e) =>
                    // should never get here since this filter is meant to be after (above)
                    // the `ExceptionMappingFilter` which translates exceptions to responses.
                    info(logFormatter.formatException(request, e, elapsed()))
                }
              }
            )
          }
        )
      }
    )
}
