package lib

import akka.actor.Actor._
import akka.actor.Scheduler
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime


object Backend {
  val listener = actorOf[EventListener].start()
  val calculator = actorOf[Calculator].start()

  val mqReader = new MqReader(listener)

  def start() {
    Scheduler.restart()
    Scheduler.schedule(listener, TruncateClickStream(), 1, 1, TimeUnit.MINUTES)
    Scheduler.schedule(listener, SendClickStreamTo(calculator), 5, 5, TimeUnit.SECONDS)
    spawn {
      mqReader.start()
    }

    listener ! Event("1.1.1.1", new DateTime(), "/dummy", "GET", 200, Some("http://www.google.com"), "my agent", "geo!")
  }

  def stop() {
    Scheduler.shutdown()
    mqReader.stop()
    listener.stop()
  }

  def currentStats = (calculator ? GetStats()).as[(List[HitReport], ListsOfStuff)]

  def currentLists = currentStats.map(_._2)

  def currentHits = currentStats.map(_._1).get
}