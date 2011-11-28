package lib

import akka.actor._
import akka.event.EventHandler
import collection.GenSeq
import org.scala_tools.time.Imports._

case class TruncateClickStream()
case class GetClickStream()


// it's very very important that this class is totally immutable!
case class ClickStream(allClicks: GenSeq[Event], lastUpdated: DateTime, firstUpdated: DateTime) {
  lazy val userClicks = allClicks filterNot isBot

  def +(e: Event) = copy(allClicks = e +: allClicks, lastUpdated = e.dt)

  def removeEventsBefore(dt: DateTime) = copy(
    allClicks = allClicks.filterNot(_.dt < dt),
    firstUpdated = if (firstUpdated > dt) firstUpdated else dt
  )

  def ageMs = DateTime.now.millis - lastUpdated.millis

  private def isBot(e: Event) = e.userAgent.startsWith("facebookexternalhit")

  lazy val timePeriodMillis = lastUpdated.millis - firstUpdated.millis
  lazy val secs = timePeriodMillis / 1000
}


class EventListener extends Actor {
  var clickStream = ClickStream(Nil.par, DateTime.now, DateTime.now)

  protected def receive = {
    case e: Event => {
      clickStream += e
    }

    case TruncateClickStream() => {
      EventHandler.info(this, "Truncating click stream (size=%d)" format clickStream.allClicks.size)
      clickStream = clickStream.removeEventsBefore(DateTime.now - 15.minutes)
      EventHandler.info(this, "Truncated click stream (size=%d)" format clickStream.allClicks.size)
    }

    case GetClickStream() => self.channel ! clickStream

  }
}









