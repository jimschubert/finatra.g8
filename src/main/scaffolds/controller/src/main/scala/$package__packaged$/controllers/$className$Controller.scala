package $package$.controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import javax.inject.{Inject, Singleton}

@Singleton
class $className$Controller @Inject()() extends Controller {
  get("/$className$") { request: Request =>
  	response.ok.json(Map("message" -> "success"))
  }
}
