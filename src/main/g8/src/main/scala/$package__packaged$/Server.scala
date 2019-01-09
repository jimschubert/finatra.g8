package $package$

import $package$.modules.ServiceSwaggerModule
import $package$.controllers.AdminController
import $package$.controllers.MainController
import $package$.filters.CommonFilters
import $package$.util.AppConfigLib._
import com.jakehschwartz.finatra.swagger.DocsController
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.util.Var
import monix.execution.Scheduler
import monix.execution.schedulers.SchedulerService
import perfolation._

object ServerMain extends Server

class Server extends HttpServer {
  val health = Var("good")

  implicit lazy val scheduler: SchedulerService = Scheduler.io("$package$")

  override protected def modules = Seq(ServiceSwaggerModule)

  override def defaultHttpPort = getConfig[String]("FINATRA_HTTP_PORT").fold(":9999")(x => p":$"$"$x")
  override val name            = "$package$-$name;format="Camel"$"

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .filter[CommonFilters]
      .add[DocsController]
      .add[AdminController]
      .add[MainController]
  }
}
