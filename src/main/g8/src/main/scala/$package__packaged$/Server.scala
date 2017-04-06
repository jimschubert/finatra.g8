package $package$

import com.github.xiaodongw.swagger.finatra.{SwaggerController, WebjarsController}
import $package$.controllers.AdminController
import $package$.swagger.ProjectSwagger
import $package$.util.AppConfigLib._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.modules.FinatraJacksonModule
import com.twitter.inject.annotations.Lifecycle
import $package$.controllers.MainController
import io.swagger.converter.ModelConverters
import com.twitter.util.Var
import io.swagger.jackson.ModelResolver
import io.swagger.models.{Contact, Info}

object ServerMain extends Server

class Server extends HttpServer {
  val health         = Var("good")
  val serviceVersion = flag[String]("service.version", "NA", "the version of service")

  override def defaultFinatraHttpPort = getConfig[String]("FINATRA_HTTP_PORT").fold(":9999")(x => s":$"$"$x")
  override val name                   = "$package$ $name;format="Camel"$"

// Swagger JSON support
  ModelConverters
    .getInstance()
    .addConverter(new ModelResolver(jacksonModule.asInstanceOf[FinatraJacksonModule].provideScalaObjectMapper(null)))

  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      .add[WebjarsController]
      .add[AdminController]
      .add(new SwaggerController(swagger = ProjectSwagger))
      .add[MainController]
  }

  @Lifecycle protected override def postWarmup(): Unit = {
    super.postWarmup()

    val info = new Info()
      .contact(new Contact().name("$maintainer_name$").email("$maintainer_email$"))
      .description(
        "**$name$** - $service_description$.")
      .version(serviceVersion())
      .title("$name$ API")

    ProjectSwagger.info(info)
  }
}
