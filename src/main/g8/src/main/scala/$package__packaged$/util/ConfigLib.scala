package $package$.util

import com.netaporter.uri.Uri
import com.netaporter.uri.Uri.parse

import scala.util.control.Exception._

trait ConfigLib {
  def getConfig[T](k: String)(implicit dataConverter: DataConverter[T]): Option[T]
}

object AppConfigLib extends ConfigLib {
  def getConfig[T](k: String)(implicit dc: DataConverter[T]): Option[T] =
    Option(System.getenv(k)).flatMap(dc.convert)
}

trait DataConverter[T] {
  def convert(v: String): Option[T]
}

class IntDataConverter extends DataConverter[Int] {
  def convert(v: String): Option[Int] = allCatch.opt(v.toInt)
}

class StringDataConverter extends DataConverter[String] {
  def convert(v: String): Option[String] = Some(v)
}

class BooleanDataConverter extends DataConverter[Boolean] {
  def convert(v: String): Option[Boolean] = allCatch.opt(v.toBoolean)
}

// TODO - should handle the exception cases
class UriDataConverter extends DataConverter[Uri] {
  def convert(v: String): Option[Uri] = Some(parse(v))
}

object DataConverter {
  implicit val intConverter  = new IntDataConverter()
  implicit val strConverter  = new StringDataConverter()
  implicit val boolConverter = new BooleanDataConverter()
  implicit val uriConverter  = new UriDataConverter()
}
