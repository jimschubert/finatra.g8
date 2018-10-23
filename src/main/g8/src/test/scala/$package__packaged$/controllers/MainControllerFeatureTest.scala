package $package$.controllers

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import $package$.Server

class MainControllerFeatureTest extends FeatureTest {
  val serviceVersion: String = "0.9.9"

  override val server: EmbeddedHttpServer =
    new EmbeddedHttpServer(twitterServer = new Server, flags = Map("service.version" -> serviceVersion))

  test("Server should respond") {
    server.httpGet(path = "/tstmsg/Richard", andExpect = Ok, withJsonBody = """{"message":"Hello, Richard"}""")
    server.httpGet(path = "/tstmsg/anonymous", andExpect = Ok, withJsonBody = """{"message":"Your name, please?"}""")
    server.httpGet(path = "/tstmsg/unknown", andExpect = BadRequest)
  }
}
