package $package$.util

object PipeOperator {

  implicit class Pipe[T](val v: T) extends AnyVal {
    def |>[U](f: T => U) = f(v)

    // Additional suggestions:
    def $$[U](f: T => U): T = {
      f(v); v
    }

    def #!(str: String = ""): T = {
      println(s"$str:$v"); v
    }
  }

}