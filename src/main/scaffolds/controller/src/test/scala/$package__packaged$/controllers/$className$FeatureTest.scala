package $package$.controllers

import com.twitter.finagle.http.Status.Ok
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import $package$.Server

class $className$ControllerFeatureTest extends FeatureTest {

  override val server = new EmbeddedHttpServer(new Server)

  "/$className$" should {
    "respond" in {
      server.httpGet(
        path = "/$className$",
        andExpect = Ok,
        withBody = "{\"message\":\"success\"}")
    }
  }
}