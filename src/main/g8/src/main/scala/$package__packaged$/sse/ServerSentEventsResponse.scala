package $package$.sse

import cats.Show
import com.twitter.concurrent.AsyncStream
import com.twitter.finatra.http.response.StreamingResponse
import com.twitter.io.Buf
import $package$.util.PipeOperator._
import perfolation._

object ServerSentEventsResponse {
  def apply[A](asyncStream: => AsyncStream[ServerSentEvent[A]])(
      implicit s: Show[A]): StreamingResponse[ServerSentEvent[A], Unit] =
    StreamingResponse[ServerSentEvent[A]](
      toBuf = serverSentEvent2Buf[A],
      headers = Map("Content-Type" -> "text/event-stream", "Cache-Control" -> "no-cache")
    )(asyncStream)

  private def serverSentEvent2Buf[A](e: ServerSentEvent[A])(implicit s: Show[A]): Buf = {
    Buf.Utf8(
      s.show(e.data)
        .|>(d => p"data:$"$"$d\n")
        .|>(d => p"$"$"$d$"$"${e.id.map(i => p"id:$"$"$i\n").getOrElse("")}")
        .|>(d => p"$"$"$d$"$"${e.event.map(ev => p"event:$"$"$ev\n").getOrElse("")}")
        .|>(d => p"$"$"$d$"$"${e.retry.map(re => p"retry:$"$"$re\n").getOrElse("")}"))
  }
}

case class ServerSentEvent[A](
    data: A,
    id: Option[String] = None,
    event: Option[String] = None,
    retry: Option[Long] = None
)
