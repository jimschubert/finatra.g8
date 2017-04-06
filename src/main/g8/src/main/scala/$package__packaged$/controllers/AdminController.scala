package $package$.controllers

import javax.inject.Inject

import $package$.ServerMain._
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.inject.annotations.Flag

class AdminController @Inject()(@Flag("service.version") version: String) extends Controller {
  get("/health") { request: Request =>
    if (health() == "good") response.ok else response.serviceUnavailable
  }

  get("/version") { request: Request =>
    version
  }
}
