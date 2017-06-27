package $package$

import $package$.modules.ServiceSwaggerModule
import $package$.controllers.AdminController
import $package$.util.AppConfigLib._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.annotations.Lifecycle
import $package$.controllers.MainController
import com.twitter.util.Var
import com.jakehschwartz.finatra.swagger.DocsController

object ServerMain extends Server

class Server extends HttpServer {
  val health = Var("good")

  override protected def modules = Seq(ServiceSwaggerModule)

  override def defaultFinatraHttpPort = getConfig[String]("FINATRA_HTTP_PORT").fold(":9999")(x => s":$"$"$x")
  override val name                   = "$package$ $name;format="Camel"$"

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .add[DocsController]
      .add[AdminController]
      .add[MainController]
  }
}
