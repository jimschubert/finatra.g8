package $package$.problem

import cats.syntax.option._
import io.lemonlabs.uri.Uri
import org.scalatest.{FunSuite, Matchers}
import ProblemResponse._
import com.github.mehmetakiftutuncu.errors.{Errors, SimpleError}
import $package$.problem.SampleExtension.{AccountId, TraceId}
import com.twitter.finagle.http.Response
import com.twitter.finagle.http.Status._
import io.estatico.newtype.macros.newtype

object SampleExtension {
  @newtype final case class TraceId(id: String)
  @newtype final case class AccountId(id: String)
}

// sbt "testOnly $package$.problem.ProblemResponseSpec"
class ProblemResponseSpec extends FunSuite with Matchers {
  final case class SampleExtension(traceId: TraceId, accountId: AccountId)

  test("Should generate proper RFC7807 error response") {
    val resp: Response = ProblemResponse(
      Problem[Errors, SampleExtension](
        Uri.parse("http://localhost:8080/errors.html"),
        Title("Resource Not Found"),
        NotFound,
        ErrorsDetail(Errors(SimpleError.notFound)).some,
        SampleExtension(TraceId("tc-2846673"), AccountId("ac-42567833")).some
      )
    )

    resp.contentType shouldBe Some("application/problem+json")
    resp.status shouldBe NotFound
    resp.contentString shouldBe """{"type":"http://localhost:8080/errors.html","title":"Resource Not Found","status":404,"detail":[{"name":"notFound"}],"extension":{"traceId":"tc-2846673","accountId":"ac-42567833"}}"""

    val anotherResp: Response = ProblemResponse(
      Problem[Errors, SampleExtension](
        Uri.parse("http://localhost:8080/errors.html"),
        Title("Failed Dependency"),
        FailedDependency
      )
    )

    anotherResp.contentType shouldBe Some("application/problem+json")
    anotherResp.status shouldBe FailedDependency
    anotherResp.contentString shouldBe """{"type":"http://localhost:8080/errors.html","title":"Failed Dependency","status":424}"""

    val lastResp: Response =
      ProblemResponse(Problem[Errors, SampleExtension](title = Title("Failed Dependency"), status = FailedDependency))

    lastResp.contentType shouldBe Some("application/problem+json")
    lastResp.status shouldBe FailedDependency
    lastResp.contentString shouldBe """{"type":"about:blank","title":"Failed Dependency","status":424}"""

    val t1Resp: Response = ProblemResponse(
      Problem[Throwable, SampleExtension](
        Uri.parse("http://localhost:8080/errors.html"),
        Title("Resource Not Found"),
        NotFound,
        ThrowableDetail(new Error("Required image not found!")).some
      )
    )

    t1Resp.contentType shouldBe Some("application/problem+json")
    t1Resp.status shouldBe NotFound
    t1Resp.contentString shouldBe """{"type":"http://localhost:8080/errors.html","title":"Resource Not Found","status":404,"detail":"java.lang.Error occurred, reason:`Required image not found!`"}"""
  }
}
