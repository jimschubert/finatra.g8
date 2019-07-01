package $package$

import com.google.inject.Stage
import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import cats.effect.IO
import java.io.File
import java.io.BufferedWriter
import java.io.FileWriter

class StartupTest extends FeatureTest {

  val server = new EmbeddedHttpServer(
    stage = Stage.PRODUCTION,
    twitterServer = new Server)

  test("server") {
    server.assertHealthy()
  }

  test("Swagger.json should be exported") {
    val swaggerContent = server.httpGet(path = "/swagger.json", andExpect = Ok).contentString

    IO(new BufferedWriter(new FileWriter(new File("swagger.json"))))
      .bracket(writer => IO(writer.write(swaggerContent)))(writer => IO(writer.close))
      .unsafeRunSync()
  }
}
