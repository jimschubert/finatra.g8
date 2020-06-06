package $package$.models.http

import com.twitter.finatra.http.annotations.RouteParam

final case class GreetingsRequest(@RouteParam name: String)
