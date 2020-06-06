package $package$.problem

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

final case class SampleExtension(traceId: TraceId, accountId: AccountId)

// sbt "testOnly $package$.problem.ProblemResponseSpec"
class ProblemResponseSpec extends FunSuite with Matchers {

  test("Should generate proper RFC7807 error response") {
    val resp: Response = ProblemResponse(
      Problem[Errors, SampleExtension](
        Uri.parse("http://localhost:8080/errors.html"),
        Title("Resource Not Found"),
        NotFound,
        ErrorsDetail(Errors(SimpleError.notFound)),
        SampleExtension(TraceId("tc-2846673"), AccountId("ac-42567833"))
      )
    )

    resp.contentType shouldBe Option("application/problem+json")
    resp.status shouldBe NotFound
    resp.contentString shouldBe """{"type":"http://localhost:8080/errors.html","title":"Resource Not Found","status":404,"detail":[{"name":"notFound"}],"extension":{"traceId":"tc-2846673","accountId":"ac-42567833"}}"""

    val anotherResp: Response = ProblemResponse(
      NoExtensionProblem[Errors](
        Uri.parse("http://localhost:8080/errors.html"),
        Title("Failed Dependency"),
        FailedDependency,
        ErrorsDetail(Errors(SimpleError.database))
      )
    )

    anotherResp.contentType shouldBe Option("application/problem+json")
    anotherResp.status shouldBe FailedDependency
    anotherResp.contentString shouldBe """{"type":"http://localhost:8080/errors.html","title":"Failed Dependency","status":424,"detail":[{"name":"database"}]}"""

    val lastResp: Response =
      ProblemResponse(
        NoExtensionProblem[Errors](
          title = Title("Failed Dependency"),
          status = FailedDependency,
          detail = ErrorsDetail(Errors(SimpleError.database))
        )
      )

    lastResp.contentType shouldBe Option("application/problem+json")
    lastResp.status shouldBe FailedDependency
    lastResp.contentString shouldBe """{"type":"about:blank","title":"Failed Dependency","status":424,"detail":[{"name":"database"}]}"""

    val t1Resp: Response = ProblemResponse(
      NoExtensionProblem[Throwable](
        Uri.parse("http://localhost:8080/errors.html"),
        Title("Resource Not Found"),
        NotFound,
        ThrowableDetail(new Error("Required image not found!"))
      )
    )

    t1Resp.contentType shouldBe Option("application/problem+json")
    t1Resp.status shouldBe NotFound
    t1Resp.contentString shouldBe """{"type":"http://localhost:8080/errors.html","title":"Resource Not Found","status":404,"detail":"java.lang.Error occurred, reason:`Required image not found!`"}"""
  }
}
