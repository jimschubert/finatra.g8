package $package$.util.oplog

import cats.Show
import mouse.option._
import perfolation._

object ExceptionError {
  implicit val exErrorShow: Show[Throwable] = Show.show(
    e => p"""{
            |     "exception": "$"$"${e.getClass.getCanonicalName}",
            |     "msg": "$"$"${Option(e.getMessage).cata(identity, "no message")}"
            |   }""".stripMargin
  )
}
