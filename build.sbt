import AssemblyKeys._

name          := "landing-site"

organization  := "scala-meetup-mvd"

version       := "0.1-20130801"

scalaVersion  := "2.10.2"

scalacOptions := Seq(
  "-unchecked", 
  "-deprecation", 
  "-feature", 
  "-encoding", "utf8",
  "-language", "postfixOps" )

resolvers ++= Seq(
  "spray nightlies" at "http://nightlies.spray.io/",
  "spray repo"      at "http://repo.spray.io/"
)

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"         % "1.2-20130801",
  "io.spray"            %   "spray-routing"     % "1.2-20130801",
  "io.spray"            %   "spray-client"      % "1.2-20130801",
  "io.spray"            %%  "spray-json"        % "1.2.5",
  "net.virtual-void"    %%  "json-lenses"       % "0.5.3",
  "com.typesafe.akka"   %%  "akka-actor"        % "2.2.0",
  "com.typesafe.akka"   %%  "akka-slf4j"        % "2.2.0",
  "org.rogach"          %%  "scallop"           % "0.9.3",
  "ch.qos.logback"      %   "logback-classic"   % "1.0.9",
  "ch.qos.logback"      %   "logback-core"      % "1.0.9",
  "io.spray"            %   "spray-testkit"     % "1.2-20130801"  % "test",
  "com.typesafe.akka"   %%  "akka-testkit"      % "2.2.0-RC1"     % "test",
  "org.specs2"          %%  "specs2"            % "1.14"          % "test"
)


seq(Revolver.settings: _*)

assemblySettings

