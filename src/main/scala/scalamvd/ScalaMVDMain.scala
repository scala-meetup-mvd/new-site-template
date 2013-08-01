package scalamvd

import akka.actor.{ActorSystem, Props}
import akka.pattern._
import akka.io.IO
import spray.can.Http
import scala.util.{Failure, Success}
import scala.concurrent.duration._

import akka.util.Timeout

object ScalaMVDMain extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("scala-mvd")

  // we need a timeout for the ask operation
  implicit val ts = Timeout(1 second)

  // and we need a context for the futures to run on
  // (the .onComplete method needs it)
  import system.dispatcher


  // create and start our service actor
  val service = system.actorOf(Props[RootService], "site-root")

  // start a new HTTP server on port 8080 with our service actor as the handler
  (IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080))
    .onComplete{
      case Success(s) => println(s"Bound to port 8080 $s")
      case Failure(x) =>
        println(s"ERROR ${x.getMessage}")
        system.shutdown()
    }


  if ( args contains "--DEBUG" ) {
    Console.readLine("Press enter to stop")
    println("Stopping actor system ...")
    system.shutdown()
    system.awaitTermination()
  }

}

