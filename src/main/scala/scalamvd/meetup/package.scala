package scalamvd

import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.util.Timeout
import akka.pattern._
import akka.io.IO

import concurrent.duration._

import spray.can.Http
import spray.client.pipelining._
import spray.httpx.encoding.{Deflate, Gzip}

import spray.json._
import DefaultJsonProtocol._
import scala.concurrent.Future

/** Meetup api machinery. */
package object meetup {

  /** Information needed to make requests. */
  case class GroupInfo(
    id:     String,
    key:    String,
    url:    String,
    page:   Int = 3,
    cache:  Boolean = false
  )


  /** Low level http interface with the meetup api. */
  trait MeetupHttpClient extends Actor {

    val group: GroupInfo

    private implicit val exCtx = context.system.dispatcher

    /** Yields a Future[JsValue] */
    def requestEvents() : Future[JsValue] =
      for {
        client  <- httpClient(context.system)
        rp      <- client(eventsRequestURI)
        e       = rp.entity
        json    = e.asString.asJson
      } yield json

    private def httpClient(system:ActorSystem) = {

      implicit val t = Timeout(30 second)

      for {
        Http.HostConnectorInfo(connector, _) <-
          IO(Http)(system).ask (
            Http.HostConnectorSetup("api.meetup.com", port = 443, sslEncryption = true)
          )
      } yield (
        addHeader("x-requested-by", "scala.meetup.uy" ) // Just so I can show you
          ~> sendReceive(connector)
          ~> decode(Deflate)
      )
    }

    private lazy val eventsRequestURI =
      Get(s"/2/events?key=${group.key}&sign=true&page=${group.page}&group_id=${group.id}")

    private lazy val commentsRequestURI = ???

  }

  case class CachedResponse(timestamp: Long, data: JsValue)

  object CachedResponse {
    val invalidationThreshold = 1000 * 60 * 60
    def apply(value: JsValue) = new CachedResponse(System.currentTimeMillis(), value)
    def isValid(cr:CachedResponse) = {
      val current = System.currentTimeMillis()
      current - cr.timestamp < CachedResponse.invalidationThreshold
    }
  }



}
