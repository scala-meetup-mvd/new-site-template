package scalamvd

import spray.util._
import spray.routing._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class RootService
extends HttpServiceActor
  with SiteRoutes   // Static routes
  with MeetupRoutes
  with SprayActorLogging {

  // this actor only runs our routes
  def receive = runRoute(siteRoute ~ meetupRoutes)

}

/** Static routes and a test entry point.
  *
  *  Defined separately to enable testing without an actor system.
  *
  */
trait SiteRoutes extends HttpService {

  def siteRoute = testRoute ~ staticRoute

  def staticRoute =
    path("")( getFromResource("web/index.html") ) ~
              getFromResourceDirectory("web")

  def testRoute =
    path("test") ( get(complete("test")) )

}

