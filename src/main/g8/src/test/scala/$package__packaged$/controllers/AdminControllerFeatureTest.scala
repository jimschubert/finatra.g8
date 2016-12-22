package $package$.controllers

import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import com.twitter.finagle.http.Status._
import $package$.Server

class AdminControllerFeatureTest extends FeatureTest {
  val serviceVersion = "0.9.9"

  override val server =
    new EmbeddedHttpServer(twitterServer = new Server, flags = Map("service.version" -> serviceVersion))

  "Server" should {
    "return `OK` (health check) if the service is on" in {
      server.httpGet(path = "/health", andExpect = Ok)
    }

    "return service version" in {
      server.httpGet(path = "/version", andExpect = Ok, withBody = serviceVersion)
    }
  }
}