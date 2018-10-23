package $package$.services

import com.twitter.inject.Logging
import io.catbird.util.Rerunnable
import javax.inject.Singleton
import perfolation._

@Singleton
class SampleMessageService extends RerunnableService[String, SMessage] with Logging {
  override def apply(request: String): Rerunnable[SMessage] =
    request match {
      case "anonymous" => Rerunnable.const(FailureMessage("Your name, please?"))
      case "unknown"   => Rerunnable.raiseError(new Error("UNKNOWN-NOT-ALLOWED"))
      case x           => Rerunnable.const(GreetingsMessage(p"Hello, $"$"$x"))
    }
}

sealed trait SMessage

final case class GreetingsMessage(message: String) extends SMessage
final case class FailureMessage(message: String)   extends SMessage
