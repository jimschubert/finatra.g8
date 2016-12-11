package $package$.controllers

import javax.inject.Inject

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.inject.annotations.Flag

class AdminController @Inject()(@Flag("service.version") version: String) extends Controller {
  get("/health") { request: Request =>
    response.ok
  }

  get("/version") { request: Request =>
    version
  }
}