package lib

import java.util.concurrent.TimeUnit
import com.gu.openplatform.contentapi.model.Content
import org.joda.time.{Duration, DateTime}
import akka.actor._
import akka.util.duration._
import akka.dispatch.{Await, Future}
import akka.util.Timeout
import concurrent.ops
import ops._

object Backend {
  val system = ActorSystem("liveDashboard")
  val listener = system.actorOf(Props[ClickStreamActor], name = "clickStreamListener")
  val calculator = system.actorOf(Props[Calculator], name = "calculator")
  val searchTerms = system.actorOf(Props[SearchTermActor], name = "searchTermProcessor")
  val latestContent = system.actorOf(Props[LatestContentActor], name = "latestContent")

  val mqReader = new MqReader(listener :: searchTerms :: Nil)

  def start() {
    system.scheduler.schedule(1 minute, 1 minute, listener, ClickStreamActor.TruncateClickStream)
    system.scheduler.schedule(5 seconds, 5 seconds, listener, ClickStreamActor.SendClickStreamTo(calculator))
    system.scheduler.schedule(5 seconds, 10 seconds, latestContent, LatestContentActor.Refresh)

    spawn {
      mqReader.start()
    }

    listener ! Event("1.1.1.1", new DateTime(), "/dummy", "GET", 200, Some("http://www.google.com"), "my agent", "geo!")
    searchTerms ! Event("1.1.1.1", new DateTime(), "/search?q=dummy&a=b&c=d%2Fj", "GET", 200, Some("http://www.google.com"), "my agent", "geo!")
  }

  def stop() {
    mqReader.stop()
    system.shutdown()
  }

  // So this is a bad way to do this, should use akka Agents instead (which can read
  // without sending a message.)

  implicit val timeout = Timeout(5 seconds)

  def currentStats = Await.result( (calculator ? Calculator.GetStats).mapTo[(List[HitReport], ListsOfStuff)], 5 seconds)

  def currentLists = currentStats._2

  def currentHits = currentStats._1

  def liveSearchTermsFuture = (searchTerms ? SearchTermActor.GetSearchTerms).mapTo[List[GuSearchTerm]]
  def liveSearchTerms = Await.result(liveSearchTermsFuture, timeout.duration)

  def last24hoursOfContentFuture = (latestContent ? LatestContentActor.Get).mapTo[List[Content]]
  def last24hoursOfContent = Await.result(last24hoursOfContentFuture, timeout.duration)

  def minutesOfData = {
    val currentData = currentLists
    new Duration(currentData.firstUpdated, currentData.lastUpdated).getStandardMinutes
  }
}