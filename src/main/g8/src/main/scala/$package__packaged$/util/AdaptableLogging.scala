package $package$.util

import $package$.util.AppConfigLib._
import $package$.util.PipeOperator._
import com.twitter.inject.Logging

trait AdaptableLogging extends Logging {
  def log(t: Option[Throwable] = None, msg: => Any): Unit =
    getConfig[String]("LOG_LEVEL")
      .map(_.toLowerCase)
      .|>(
        logLevel =>
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
      )
}
