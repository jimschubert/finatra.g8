package $package$.controllers

import javax.inject.Inject

import $package$.ServerMain._
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.inject.annotations.Flag
import com.jakehschwartz.finatra.swagger.SwaggerController
import io.swagger.models.Swagger

class AdminController @Inject()(s: Swagger, @Flag("service.version") version: String) extends SwaggerController {
  implicit protected val swagger = s

  getWithDoc("/health") { o =>
    o.summary("Health checking API")
      .tag("Health")
      .responseWith(200, "The service is healthy")
      .responseWith(500, "The service is not healthy")
  } { request: Request =>
    if (health() == "good") response.ok else response.serviceUnavailable
  }

  getWithDoc("/version") { o =>
    o.summary("Service version API")
      .tag("Version")
      .responseWith(200, "The service's version")
  } { request: Request =>
    response.ok.contentType("text/plain").body(version)
  }
}
