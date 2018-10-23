package $package$.controllers

import $package$.models.http.GreetingsRequest
import javax.inject.{Inject, Singleton}
import com.jakehschwartz.finatra.swagger.SwaggerController
import io.swagger.models.Swagger
import $package$.services.SampleMessageService

@Singleton
class MainController @Inject()(s: Swagger, msgSvc: SampleMessageService) extends SwaggerController {
  implicit protected val swagger = s

  getWithDoc("/tstmsg/:name") { o =>
    o.summary("Acquiring greetings message")
      .tag("Greetings")
      .routeParam[String]("name", "Greetings name")
      .responseWith(200, "Hello message")
  } { request: GreetingsRequest =>
    msgSvc(request.name).map(response.ok.json).run.handle {
      case _: Error => response.badRequest
    }
  }}
