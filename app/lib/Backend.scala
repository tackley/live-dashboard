package lib

import akka.actor.Actor._
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime
import akka.actor.{Supervisor, Scheduler}
import akka.config.Supervision._
import com.gu.openplatform.contentapi.model.Content


object Backend {
  val listener = actorOf[ClickStreamActor].start()
  val calculator = actorOf[Calculator].start()
  val searchTerms = actorOf[SearchTermActor].start()
  val latestContent = actorOf[LatestContentActor].start()

  val mqReader = new MqReader(listener :: searchTerms :: Nil)

  def start() {
    Scheduler.restart()
    Scheduler.schedule(listener, TruncateClickStream(), 1, 1, TimeUnit.MINUTES)
    Scheduler.schedule(listener, SendClickStreamTo(calculator), 5, 5, TimeUnit.SECONDS)
    Scheduler.schedule(latestContent, LatestContentActor.Refresh(), 5, 10, TimeUnit.SECONDS)
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

  // So this is a bad way to do this, should use akka Agents instead (which can read
  // without sending a message.)

  def currentStats = (calculator ? GetStats()).as[(List[HitReport], ListsOfStuff)]

  def currentLists = currentStats.map(_._2)

  def currentHits = currentStats.map(_._1).get

  def liveSearchTerms = (searchTerms ? GetSearchTerms()).as[List[GuSearchTerm]]

  def last24hoursOfContent = (latestContent ? LatestContentActor.Get()).as[List[Content]]
}