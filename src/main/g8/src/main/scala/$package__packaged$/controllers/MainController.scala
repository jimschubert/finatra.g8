package $package$.controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class MainController extends Controller {
  get("/") { request: Request =>
  	response.ok.json(Map("message" -> "success"))
  }
}
