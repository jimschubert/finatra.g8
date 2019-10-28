package $package$.util.oplog

import cats.Show
import ExceptionError._
import $package$.util.oplog.OperationLog.PriorityLevel._
import $package$.util.oplog.OperationLog._
import com.twitter.inject.app.TestInjector
import com.twitter.inject.{Injector, IntegrationTest}
import com.twitter.util.Time
import io.catbird.util.Rerunnable
import io.catbird.util.effect._
import perfolation._

// sbt clean "testOnly $package$.util.oplog.OpLoggerTest"
class OpLoggerTest extends IntegrationTest {
  override protected def injector: Injector = TestInjector().create

  test("OpLogger should successfully write operation logs") {
    val now = Time.now.inMillis

    final case class SampleData(accountId: String, txId: String, response: String)

    val sampleNoErrorLog = NoErrorOperationLog[SampleData](
      team      = Team("architect"),
      service   = Service("Sample Service"),
      timestamp = now,
      msgLevel  = P0,
      data = Data[SampleData](
        SampleData(
          accountId = "45909f22-f6fe-11e9-a3f5-784f436a2b87",
          txId      = "70915c16-f6fe-11e9-9598-784f436a2b87",
          response  = "login succeeded"
        )
      )
    )

    implicit val sdShow: Show[SampleData] = Show.show(data => p"""{ "accountId": "$"$"${data.accountId}",
                                                                 |     "txId": "$"$"${data.txId}",
                                                                 |     "response": "$"$"${data.response}"
                                                                 | }""".stripMargin)

    object SampleApp extends OpLogger {
      def writeLog(d: NoErrorOperationLog[SampleData]): String = await(logWithNoError[Rerunnable, SampleData](d).run)
    }

    SampleApp.writeLog(sampleNoErrorLog) shouldBe
      p"""
         |{
         |  "team": "architect",
         |  "service": "Sample Service",
         |  "timestamp": $"$"$now,
         |  "traceId": "null",
         |  "msgLevel": "P0",
         |  "data": { "accountId": "45909f22-f6fe-11e9-a3f5-784f436a2b87",
         |     "txId": "70915c16-f6fe-11e9-9598-784f436a2b87",
         |     "response": "login succeeded"
         | }
         |}
         |""".stripMargin

    val sampleWithErrorLog = WithErrorOperationLog[Throwable, SampleData](
      team      = Team("architect"),
      service   = Service("Sample Service"),
      timestamp = now,
      msgLevel  = P0,
      data = Data[SampleData](
        SampleData(
          accountId = "45909f22-f6fe-11e9-a3f5-784f436a2b87",
          txId      = "70915c16-f6fe-11e9-9598-784f436a2b87",
          response  = "login failed"
        )
      ),
      error = Error(new RuntimeException("User login failed!"))
    )

    object SampleErrorApp extends OpLogger {
      def writeErrorLog(d: WithErrorOperationLog[Throwable, SampleData]): String =
        await(logWithError[Rerunnable, Throwable, SampleData](d).run)
    }

    SampleErrorApp.writeErrorLog(sampleWithErrorLog) shouldBe
      p"""
         |{
         |  "team": "architect",
         |  "service": "Sample Service",
         |  "timestamp": $"$"$now,
         |  "traceId": "null",
         |  "msgLevel": "P0",
         |  "data": { "accountId": "45909f22-f6fe-11e9-a3f5-784f436a2b87",
         |     "txId": "70915c16-f6fe-11e9-9598-784f436a2b87",
         |     "response": "login failed"
         | },
         |  "error": {
         |     "exception": "java.lang.RuntimeException",
         |     "msg": "User login failed!"
         |   }
         |}
         |""".stripMargin
  }
}
