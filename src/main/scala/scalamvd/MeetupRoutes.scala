package scalamvd


import java.util.Date

import spray.routing.HttpServiceActor

import akka.actor.ActorRef
import akka.pattern.ask

import akka.util.Timeout
import concurrent.duration._

import scalamvd.meetup.{GroupInfo, MeetupApiClient, MeetupHttpClient}

import scala.util.{Failure, Success}
import spray.util.SprayActorLogging

import spray.json._
import DefaultJsonProtocol._
import scala.concurrent.Future

import spray.httpx.SprayJsonSupport._
import spray.http.MediaTypes._

/** Exposes meetup related resources.
  *
  *  Delegates to the MeetupApiClient actor.
  */
trait MeetupRoutes
  extends HttpServiceActor
  with SprayActorLogging {

  val group: GroupInfo

  import context.{watch, actorOf}

  // this timeout will affect all operations in it's scope.
  private implicit val waitAtMost = Timeout(45 seconds)

  // all futures within the scope will run in this context.
  private implicit val exCtx = context.dispatcher

  import context.{watch, actorOf}

  val meetupApiClient =  watch(actorOf(MeetupApiClient(group), "MEETUP_API_CLIENT"))

  def meetupRoutes =
    pathPrefix("meetup") (
      root ~ events ~ comments ~ test
    )

  def root =
    path(""){
      complete("We have events and comments, dude")
    }

  def events =
    path("events") {
      respondWithMediaType( `application/json` ) {
        complete {
          (meetupApiClient ? MeetupApiClient.Msg.GetEvents).mapTo[String]
        }
      }
    }

  def comments  =
    path( "comments" ) {
      complete("TO-DO") // TODO implement reading meetup api
    }

  def test =
    path("test") {
      complete( "Ok:"+(new Date).getTime )
    }



}
