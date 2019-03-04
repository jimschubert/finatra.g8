package $package$.client

import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.conversions.DurationOps._
import $package$.util.AppConfigLib._
import $package$.util.PipeOperator._

import mouse.boolean._

object RichHttpClient {
  /* Public */
  val shouldEnableFastFail: Boolean =
    getConfig[Boolean]("FAIL_FAST_ENABLE").getOrElse(false)

  def newClientService(dest: String): Service[Request, Response] =
    Http.client.withSession
      .maxLifeTime(20.seconds)
      .withSessionPool
      .ttl(10.seconds)
      .|>(c => (!shouldEnableFastFail).fold(c.withSessionQualifier.noFailFast, c))
      .newService(dest)

  def newSslClientService(sslHostname: String, dest: String): Service[Request, Response] = {
    val shouldValidate = getConfig[Boolean]("TLS_VALIDATION").getOrElse(true)

    Http.client.withSession
      .maxLifeTime(20.seconds)
      .withSessionPool
      .ttl(10.seconds)
      .|>(c => (!shouldEnableFastFail).fold(c.withSessionQualifier.noFailFast, c))
      .|>(c => shouldValidate.fold(c.withTls(sslHostname), c.withTlsWithoutValidation))
      .newService(dest)
  }
}
