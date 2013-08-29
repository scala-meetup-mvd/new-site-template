package scalamvd

import scalamvd.meetup.GroupInfo
import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.io.IO
import spray.can.Http
import scala.util.{Failure, Success}
import scala.concurrent.duration._

import akka.util.Timeout

import org.rogach.scallop._

object ScalaMVDMain extends App {

  val conf = new Conf(args)

  val group = GroupInfo (
    id    = conf.id(),
    key   = conf.key(),
    url   = conf.url(),
    cache = conf.cache()
  )

  // we need an ActorSystem to host our application in
  val system = ActorSystem("scala-mvd")

  // create and start our service actor
  val service = system.actorOf(Router(group), "site-router")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http)(system) ! Http.Bind(service, interface = "0.0.0.0", port = 8080)

  if ( conf.debug() ) {
    Console.readLine( "Press enter to stop" )
    println( "Stopping actor system ..." )
    system.shutdown()
    system.awaitTermination()
  } else {
    sys addShutdownHook {
      system.shutdown()
      // I don't think this is going to work in a shutdown hook.
      system.awaitTermination()
    }
  }

}

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {

  val id    = opt[String](required=true)
  val key   = opt[String](required=true)
  val url   = opt[String](required=true)
  val cache = opt[Boolean](default = Some(true))

  val debug = opt[Boolean]( argName = "debug", default = Some(false) )

}





