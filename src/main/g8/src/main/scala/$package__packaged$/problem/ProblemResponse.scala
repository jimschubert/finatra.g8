package $package$.problem

import cats.Show
import cats.instances.string._
import cats.syntax.eq._
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.github.mehmetakiftutuncu.errors.Errors
import $package$.problem.ProblemResponse._
import $package$.util.PipeOperator._
import com.twitter.finagle.http.{Response, Status}
import com.twitter.finatra.http.response.SimpleResponse
import com.twitter.finatra.json.FinatraObjectMapper
import io.estatico.newtype.macros.newtype
import io.lemonlabs.uri.Uri
import mouse.boolean._
import perfolation._

final case class Problem[T, E](`type`: Uri = Uri.parse("about:blank"),
                               title: Title,
                               status: Status,
                               detail: Detail[T],
                               extension: E)

final case class NoExtensionProblem[T](`type`: Uri = Uri.parse("about:blank"),
                                        title: Title,
                                        status: Status,
                                        detail: Detail[T])

object ProblemResponse {
  private[this] val mapper = FinatraObjectMapper
    .create()
    .$"$"$$"$"$(_.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE))

    def apply[T, E](problem: Problem[T, E])(implicit show: Show[T]): Response =
      SimpleResponse(
        problem.status,
        p"""{"type":"$"$"${problem.`type`
          .toString()}","title":"$"$"${problem.title}","status":$"$"${problem.status.code},"detail":$"$"${problem.detail.detailMsg},"extension":$"$"${mapper
          .writeValueAsString(problem.extension)}}"""
      ).$"$"$$"$"$ { r =>
        r.contentType = "application/problem+json"
      }

    def apply[T](problem: NoExtensionProblem[T])(implicit show: Show[T]): Response =
      SimpleResponse(
        problem.status,
        p"""{"type":"$"$"${problem.`type`
          .toString()}","title":"$"$"${problem.title}","status":$"$"${problem.status.code},"detail":$"$"${problem.detail.detailMsg}}"""
      ).$"$"$$"$"$ { r =>
        r.contentType = "application/problem+json"
      }

  implicit val errorsShow: Show[Errors] = (t: Errors) => t.represent(includeWhen = false)
  implicit val throwableShow: Show[Throwable] = (t: Throwable) =>
    p""""$"$"${t.getClass.getName} occurred$"$"${t.getMessage.|>(m => (m =!= "").fold(p", reason:`$"$"$m`", ""))}""""

  @newtype final case class Title(title: String)

  final case class ErrorsDetail(e: Errors) extends Detail[Errors] {
    override def detailMsg(implicit show: Show[Errors]): String = show.show(e)
  }

  final case class ThrowableDetail(t: Throwable) extends Detail[Throwable] {
    override def detailMsg(implicit show: Show[Throwable]): String = show.show(t)
  }
}
