package lib

import akka.actor.Actor._
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime
import akka.actor.{Supervisor, Scheduler}
import akka.config.Supervision._



object Backend {
  val listener = actorOf[ClickStreamActor].start()
  val calculator = actorOf[Calculator].start()
  val searchTerms = actorOf[SearchTermActor].start()

  val mqReader = new MqReader(listener :: searchTerms :: Nil)

  def start() {
    Scheduler.restart()
    Scheduler.schedule(listener, TruncateClickStream(), 1, 1, TimeUnit.MINUTES)
    Scheduler.schedule(listener, SendClickStreamTo(calculator), 5, 5, TimeUnit.SECONDS)
    spawn {
      mqReader.start()
    }

    listener ! Event("1.1.1.1", new DateTime(), "/dummy", "GET", 200, Some("http://www.google.com"), "my agent", "geo!")
    searchTerms ! Event("1.1.1.1", new DateTime(), "/search?q=dummy&a=b&c=d%2Fj", "GET", 200, Some("http://www.google.com"), "my agent", "geo!")
  }

  def stop() {
    Scheduler.shutdown()
    mqReader.stop()
    listener.stop()
  }

  def currentStats = (calculator ? GetStats()).as[(List[HitReport], ListsOfStuff)]

  def currentLists = currentStats.map(_._2)

  def currentHits = currentStats.map(_._1).get

  def liveSearchTerms = (searchTerms ? GetSearchTerms()).as[List[GuSearchTerm]]
}