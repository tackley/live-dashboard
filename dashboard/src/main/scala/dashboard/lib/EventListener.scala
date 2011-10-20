package dashboard.lib

import akka.actor._
import akka.event.EventHandler
import collection.GenSeq
import org.scala_tools.time.Imports._
import dashboard.comet.{TheTopTen, TopTenServer}

case class TruncateClickStream()
case class GetClickStream()
case class UpdateFrontend()


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

    case UpdateFrontend() => {
      val calc = Calculator.calcTopTenPaths(clickStream)
      TopTenServer ! TheTopTen(calc)
    }
  }
}









