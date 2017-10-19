package $package$.controllers

import com.twitter.finagle.http.Request

import javax.inject.{Inject, Singleton}
import com.jakehschwartz.finatra.swagger.SwaggerController
import io.swagger.models.Swagger

@Singleton
class MainController @Inject()(s: Swagger) extends SwaggerController {
  implicit protected val swagger = s

  getWithDoc("/") { o =>
    o.summary("Acquiring greetings message")
      .tag("Greetings")
      .responseWith(200, "Hello message")
  } { request: Request =>
    response.ok.json(Map("message" -> "success"))
  }
}
