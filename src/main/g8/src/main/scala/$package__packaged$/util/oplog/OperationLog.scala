package $package$.util.oplog

import cats.Show
import $package$.util.oplog.OperationLog._
import com.twitter.util.Time
import enumeratum._
import mouse.option._
import org.slf4j.MDC
import perfolation._

import scala.collection.immutable
import scala.util.Try

final case class NoErrorOperationLog[D](
    team: Team,
    service: Service,
    timestamp: Long         = Time.now.inMillis,
    traceId: Option[String] = Try(MDC.get("traceId")).toOption,
    msgLevel: PriorityLevel,
    data: Data[D]
) {

  def toJsonString(implicit ds: Show[D]): String =
    traceId.cata(
      tId => p"""
                |{
                |  "team": "$"$"${team.name}",
                |  "service": "$"$"${service.name}",
                |  "timestamp": $"$"$timestamp,
                |  "traceId": "$"$"$tId",
                |  "msgLevel": "$"$"${msgLevel.entryName}",
                |  "data": $"$"${ds.show(data.d)}
                |}
                |""".stripMargin,
      p"""
         |{
         |  "team": "$"$"${team.name}",
         |  "service": "$"$"${service.name}",
         |  "timestamp": $"$"$timestamp,
         |  "msgLevel": "$"$"${msgLevel.entryName}",
         |  "data": $"$"${ds.show(data.d)}
         |}
         |""".stripMargin
    )
}

final case class WithErrorOperationLog[E, D](
    team: Team,
    service: Service,
    timestamp: Long         = Time.now.inMillis,
    traceId: Option[String] = Try(MDC.get("traceId")).toOption,
    msgLevel: PriorityLevel,
    data: Data[D],
    error: Error[E]
) {

  def toJsonString(implicit ds: Show[D], es: Show[E]): String =
    traceId.cata(
      tId => p"""
                |{
                |  "team": "$"$"${team.name}",
                |  "service": "$"$"${service.name}",
                |  "timestamp": $"$"$timestamp,
                |  "traceId": "$"$"$tId",
                |  "msgLevel": "$"$"${msgLevel.entryName}",
                |  "data": $"$"${ds.show(data.d)},
                |  "error": $"$"${es.show(error.e)}
                |}
                |""".stripMargin,
      p"""
         |{
         |  "team": "$"$"${team.name}",
         |  "service": "$"$"${service.name}",
         |  "timestamp": $"$"$timestamp,
         |  "msgLevel": "$"$"${msgLevel.entryName}",
         |  "data": $"$"${ds.show(data.d)},
         |  "error": $"$"${es.show(error.e)}
         |}
         |""".stripMargin
    )
}

object OperationLog {
  final case class Team(name: String)    extends AnyVal
  final case class Service(name: String) extends AnyVal
  final case class Action(name: String)  extends AnyVal

  final case class Data[D](d: D)
  final case class Error[E](e: E)

  sealed trait PriorityLevel extends EnumEntry

  object PriorityLevel extends Enum[PriorityLevel] {
    val values: immutable.IndexedSeq[PriorityLevel] = findValues

    case object P0 extends PriorityLevel
    case object P1 extends PriorityLevel
    case object P2 extends PriorityLevel
    case object P3 extends PriorityLevel
  }
}
