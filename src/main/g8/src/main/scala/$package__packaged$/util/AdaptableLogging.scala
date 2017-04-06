package $package$.util

import com.twitter.inject.Logging
import AppConfigLib._

trait AdaptableLogging extends Logging {
  private[this] val logLevel =
    getConfig[String]("LOG_LEVEL").map(_.toLowerCase).getOrElse("info")

  def log(msg: => Any, t: Option[Throwable] = None): Unit = {
    logLevel match {
      case "info"                => info(msg)
      case "debug"               => debug(msg)
      case "error"               => error(msg)
      case "warn"                => warn(msg)
      case "trace"               => trace(msg)
      case "info" if t.nonEmpty  => info(msg, t.get)
      case "debug" if t.nonEmpty => debug(msg, t.get)
      case "error" if t.nonEmpty => error(msg, t.get)
      case "warn" if t.nonEmpty  => warn(msg, t.get)
      case "trace" if t.nonEmpty => trace(msg, t.get)
    }
  }
}
