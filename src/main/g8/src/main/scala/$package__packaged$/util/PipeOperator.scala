package $package$.util

import com.twitter.inject.Logging

object PipeOperator extends Logging {
  implicit class Pipe[T](val v: T) extends AnyVal {
    def |>[U](f: T => U): U = f(v)

    // Additional suggestions:
    def $"$$"$[U](f: T => U): T = {
      f(v); v
    }

    def #!(str: String = ""): T = {
      debug(s"$"$"$str:$"$"$v"); v
    }
  }
}