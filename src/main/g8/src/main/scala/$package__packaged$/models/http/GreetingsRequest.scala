package $package$.models.http

import com.twitter.finatra.request.RouteParam

final case class GreetingsRequest(@RouteParam name: String)
