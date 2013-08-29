/**
 * Created by f on 8/4/13.
 */

import akka.actor._
import akka.pattern._
import akka.io._
import akka.util.Timeout

import scala.concurrent._
import scala.concurrent.duration._

import scala.util.{Failure, Success}
import spray.json._
import DefaultJsonProtocol._ // !!! IMPORTANT, else `convertTo` and `toJson` won't work

import spray.can.Http
import spray.http._
import spray.httpx.encoding.{Gzip, Deflate}
import spray.httpx.SprayJsonSupport._
import spray.client.pipelining._

import spray.httpx.SprayJsonSupport._


case class GroupInfo(
  id:           String,
  key:          String,
  eventLimit:   Int,
  photosLimit:  Int,
  url:          String
)

def makeGroup(key:String) =
  GroupInfo(
    id          = "6488382",
    key         =  key,
    eventLimit  = 10,
    photosLimit = 10,
    url         = "scala-meetup-mvd"
  )



class Listener extends Actor {
  def receive = {
    case d: DeadLetter ⇒ println(d)
  }
}

implicit val system = ActorSystem("test")

import system.dispatcher
implicit val ts = Timeout( 30 second )

def uri(group: GroupInfo) =
  s"/2/events?key=${group.key}&sign=true&page=${group.eventLimit}&group_id=${group.id}"


def performRequest(key:String, wait: FiniteDuration = 5.seconds) = {

  val pipeline =
    for (
      Http.HostConnectorInfo(connector, _) <-
        IO(Http).ask(
          Http.HostConnectorSetup("api.meetup.com", port = 443, sslEncryption = true)
        )(30 second)
    ) yield (
      addHeader("x-requested-by", "scala.meetup.uy" ) // Just so I can show you
      ~> encode(Gzip)
      ~> sendReceive(connector)
      ~> decode(Deflate)
    )

  val rq = Get(uri(makeGroup(key)))

  for {
    p   <- pipeline
    rp  <- p(rq)
    e     = rp.entity
    json  = e.asString.asJson
  } yield json

}

//val jsonF = performRequest("XXXX")


