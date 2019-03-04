package $package$.util

import com.twitter.inject.Logging
import AppConfigLib._

trait AdaptableLogging extends Logging {
  private[this] val logLevel = getConfig[String]("LOG_LEVEL").map(_.toLowerCase)

  def log(t: Option[Throwable] = None, msg: => Any): Unit = {
    (logLevel, t) match {
      case (Some("info"), None)      => info(msg)
      case (Some("debug"), None)     => debug(msg)
      case (Some("error"), None)     => error(msg)
      case (Some("warn"), None)      => warn(msg)
      case (Some("trace"), None)     => trace(msg)
      case (Some("info"), Some(th))  => info(msg, th)
      case (Some("debug"), Some(th)) => debug(msg, th)
      case (Some("error"), Some(th)) => error(msg, th)
      case (Some("warn"), Some(th))  => warn(msg, th)
      case (Some("trace"), Some(th)) => trace(msg, th)
      case _                         => info(msg)
    }
  }
}
