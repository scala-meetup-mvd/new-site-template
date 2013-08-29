package scalamvd.meetup

import akka.actor._
import spray.util.SprayActorLogging

import spray.json._
import DefaultJsonProtocol._

import scala.concurrent.duration._


object MeetupApiClient {

  def apply(group: GroupInfo) = Props(classOf[MeetupApiClient], group)

  // Message protocol
  sealed trait Msg
  object Msg {
    case object GetEvents   extends Msg
    case object GetComments extends Msg
  }

}

/** Proxies calls the meetup api and performs naive caching. */
class MeetupApiClient(val group: GroupInfo)
  extends Actor
    with Stash
    with MeetupHttpClient
    with ActorLogging {

  import MeetupApiClient._

  implicit val exCtx = context.system.dispatcher

  import context.{become,unbecome}

  //
  // Mutable state is safe inside an actor.
  // GOOD_PRACTICE : make mutable stuff private
  //
  private val cache = collection.mutable.Map[Msg,CachedResponse]()

  def receive: Receive = {
    case Msg.GetEvents =>
      log.debug("Got Msg.GetEvents")
      fetchEvents(sender)
  }


  private def fetchEvents(dest: ActorRef) {

    log.debug( s"[FETCH_EVENTS] Will reply to $dest" )

    // TODO refactor to return the tuple ( dest, json )

    if( ! group.cache  ) {

      for( es <- requestEvents() ){
        log.debug(s"[FETCH_EVENTS] (NO_CACHE) replying to $dest with $es")
        // FIXME prune the response, keep only relevant data.
        dest ! es.prettyPrint
      }

    } else {

      cache.find({
        case ( Msg.GetEvents, cr ) if CachedResponse.isValid(cr) => true
        case _ => false
      }).fold (
        for(es <- requestEvents()) {
          log.debug(s"[FETCH_EVENTS] (NEW_CACHE_ENTRY) replying to $dest with $es  ")
          // FIXME prune the response, keep only relevant data.
          cache += ( Msg.GetEvents -> CachedResponse(es) )
          dest ! es.prettyPrint
        }
      ) { case (_, cr) =>
            log.debug(s"[FETCH_EVENTS] (FROM_CACHE) replying to $dest with ${cr.data} ")
            dest ! cr.data.prettyPrint
      }

    }

  }

}




