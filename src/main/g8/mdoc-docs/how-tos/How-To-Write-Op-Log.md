# How to write operation logs for service operations

The operation team need to dig into your service whilst the service runs into some issues. Thus sending helpful operation logs is very important for the issue resolving. Thus this project template includes support to help you write properly formatted op logs.

## API design

The main log data structures are provided by two case classes in the package `<your-project-package>.util.oplog`.

- `NoErrorOperationLog[D]`

  This case class represents a data structure of an operation log without an error. This can be used for specific operation needs that having the internal service states would be necessary.

  __Note__

  A developer __SHOULD NOT__ use this log type to expose the service's programming logic (data flow, etc.)

- `WithErrorOperationLog[E, D]`

  This case classs records an error your service encounters. This log can be used to help people understand a snapshot of the service internal states.

### Common data structures

The above case classes share the following data structure:

- team: The name of the owner team of this service
- service: The name of this service
- timestamp: the timestamp of the event occurred, the default is the current timestamp
- traceId: optional, if not given the lib will try to acquire one from slf4j MDC
- msgLevel: the emergency level of this log (p0, p1, p2, p3)
- data: a developer defined data structure (type D)

### Error

To the operation log library, a developer can defined his/her own Error type, since Error is a type class.

### How to generate log JSON strings and send the log

Finatra.g8 provides a trait `OpLogger` in the package `<your-project-package>.util.oplog`. This trait gives you two functions:

- `logWithNoError[F[_]: Sync, D](l: NoErrorOperationLog[D])(implicit ds: Show[D]): F[String]`

  This function is used when you just want to log an event without any error happened. The return type is an `cats.effect.Async` type class of `String`. The reason of using a cats.effect's type class is to help make sure your function is referencial transparant. Please read the following sample:

```scala mdoc
import cats.Show
import cats.effect.IO
import cats.syntax.apply._
import $package$.util.oplog.OpLogger
import $package$.util.oplog.OperationLog._
import $package$.util.oplog.OperationLog.PriorityLevel._
import $package$.util.oplog.NoErrorOperationLog

import perfolation._

final case class SampleData(accountId: String, txId: String, response: String)

val sampleNoErrorLog = NoErrorOperationLog[SampleData](
  team = Team("architect"),
  service = Service("Sample Service"),
  msgLevel = P0,
  data = Data[SampleData](SampleData(accountId = "45909f22-f6fe-11e9-a3f5-784f436a2b87",
                                     txId = "70915c16-f6fe-11e9-9598-784f436a2b87",
                                     response = "login succeeded")))

implicit val sdShow: Show[SampleData] =
  Show.show(data =>
    p"""{ "accountId": "$"$"${data.accountId}",
        |     "txId": "$"$"${data.txId}",
        |     "response": "$"$"${data.response}"
        | }""".stripMargin)

object SampleApp extends OpLogger {
  def writeLog(d: NoErrorOperationLog[SampleData]): String =
    (IO.delay("Op Log ==> ") *> logWithNoError[IO, SampleData](d)).unsafeRunSync
}

println(SampleApp.writeLog(sampleNoErrorLog))
```

- `logWithError[F[_]: Sync, E, D](l: WithErrorOperationLog[E, D])(implicit ds: Show[D], es: Show[E]): F[String]`

  As for op log with error message, the finatra.g8 template provides the support of `Throwable` type of Error (`Error[Throwable]`). This support is in the package `<your-project-package>.util.oplog.ExceptionError` Scala object. The exact support is to provide a `Show[Throwable]` type class.

  The following is an sample of creating error type of op log and sending the log.

```scala mdoc
import cats.Show
import cats.effect.IO
import cats.syntax.apply._
import $package$.util.oplog.ExceptionError._
import $package$.util.oplog.OpLogger
import $package$.util.oplog.OperationLog._
import $package$.util.oplog.OperationLog.PriorityLevel._
import $package$.util.oplog.WithErrorOperationLog

import perfolation._

val sampleWithErrorLog = WithErrorOperationLog[Throwable, SampleData](
  team = Team("architect"),
  service = Service("Sample Service"),
  msgLevel = P0,
  data = Data[SampleData](SampleData(accountId = "45909f22-f6fe-11e9-a3f5-784f436a2b87",
                                     txId = "70915c16-f6fe-11e9-9598-784f436a2b87",
                                     response = "login failed")),
  error = Error(new RuntimeException("User login failed!"))
)

object SampleErrorApp extends OpLogger {
  def writeErrorLog(d: WithErrorOperationLog[Throwable, SampleData]): String =
    (IO.delay("Error op log ==> ") *> logWithError[IO, Throwable, SampleData](d)).unsafeRunSync
}

println(SampleErrorApp.writeErrorLog(sampleWithErrorLog))
```

Underneath the OpLogger is the use of [Mr Christopher Davenport](https://github.com/ChristopherDavenport)'s [log4cats library](https://github.com/ChristopherDavenport/log4cats).
