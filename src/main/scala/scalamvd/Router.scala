package scalamvd

import spray.util._
import spray.routing._
import akka.actor.{Props, ActorRef}
import scalamvd.meetup.{GroupInfo, MeetupApiClient}

object Router {
  def apply( group: GroupInfo ) =
    Props(classOf[Router], group)

}

class Router(val group: GroupInfo)
extends HttpServiceActor
  with SiteRoutes       // Static routes
  with MeetupRoutes
  with SprayActorLogging {


  // this actor only runs our routes
  def receive = runRoute(siteRoute ~ meetupRoutes)


}


/** Static routes and a test entry point. */
trait SiteRoutes extends HttpService {

  def siteRoute = staticRoute ~ testRoute

  def staticRoute =
    path("")( getFromResource("web/index.html") ) ~
              getFromResourceDirectory("web")

  def testRoute =
    path("test") ( get(complete("test")) )

}

