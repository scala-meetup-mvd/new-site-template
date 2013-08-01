package scalamvd

import spray.routing.HttpService
import java.util.Date

/** Exposes meetup related resources.
  *
  *  It is just a proxy for the meetup api.
  */
trait MeetupRoutes extends HttpService {

  def meetupRoutes =
    pathPrefix("meetup") (
      root ~ events ~ comments ~ test
    )

  def root = path(""){
    complete("We have events and comments, dude")
  }

  def events = path("events") {
    complete( "TO-DO" ) // TODO implement reading meetup api
  }

  def comments  =
    path( "comments" ) {
      complete("TO-DO") // TODO echoes for now
    }


  def test = path("test") {
    complete( "Ok:"+(new Date).getTime )
  }


}
