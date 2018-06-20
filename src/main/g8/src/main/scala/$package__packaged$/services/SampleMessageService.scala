package $package$.services

import javax.inject.Singleton
import io.catbird.util.Rerunnable

@Singleton
class SampleMessageService extends RerunnableService[String, SMessage] {
  override def apply(request: String): Rerunnable[SMessage] = Rerunnable.const(SMessage(request))
}

final case class SMessage(message: String)
