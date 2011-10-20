package dashboard

import akka.actor._
import akka.actor.Actor._
import akka.event.EventHandler
import collection.GenSeq
import org.scala_tools.time.Imports._
import java.util.concurrent.TimeUnit


case class TruncateClickStream()
case class GetClickStream()

// it's very very important that this class is totally immutable!
case class ClickStream(clicks: GenSeq[Event], lastUpdated: DateTime) {
  def +(e: Event) = copy(clicks = e +: clicks, lastUpdated = e.dt)
  def removeEventsBefore(dt: DateTime) = copy(clicks = clicks.filterNot(_.dt < dt))

  def ageMs = DateTime.now.millis - lastUpdated.millis
}

class EventListener extends Actor {
  var clickStream = ClickStream(Nil.par, DateTime.now)

  protected def receive = {
    case e: Event => {
      //EventHandler.info(this, e)
      clickStream += e
    }

    case TruncateClickStream() => {
      EventHandler.info(this, "Truncating click stream...")
      clickStream = clickStream.removeEventsBefore(DateTime.now - 15.minutes)
      EventHandler.info(this, "Truncated click stream!")
    }

    case GetClickStream() => self.channel ! clickStream
  }
}


object Calculator {
  case class HitReport(url: String, percent: Double, hits: Int) {
    def summary = "%s %.1f%% (%d hits)" format (url, percent, hits)
  }

  def calcTopTenPaths(clickStream: ClickStream) = {
    val totalClicks = clickStream.clicks.size
    val clicksPerPath = clickStream.clicks.groupBy(_.path).map { case (k, v) => (k, v.size) }.toList
    val topTen = clicksPerPath.sortBy(_._2).reverse.take(20)

    topTen map { case (url, hitCount) => HitReport(url, hitCount.toDouble * 100 / totalClicks, hitCount )}
  }


}

object Main extends App {
  println("Hello")

  val listener = actorOf[EventListener].start()
  Scheduler.schedule(listener, TruncateClickStream(), 1, 1, TimeUnit.MINUTES)

  spawn {
    MqReader.run(listener)
  }

  while (2 + 2 != 42) {
    Thread.sleep(5000)

    for (stream <- (listener ? GetClickStream()).as[ClickStream]) {
      println("\n** clickstream size is " + stream.clicks.size + " age (ms) " + stream.ageMs)
      val topTen = Calculator.calcTopTenPaths(stream)
      topTen map { _.summary } foreach println

      println("total %.1f%% " format topTen.map(_.percent).sum)
    }

  }


}