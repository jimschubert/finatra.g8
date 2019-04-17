package $package$.problem

import cats.Show

trait Detail[T]  extends Product with Serializable {
  def detailMsg(implicit show: Show[T]): String
}
