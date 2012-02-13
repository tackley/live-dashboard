package lib

import akka.actor._
import collection.GenSeq
import org.scala_tools.time.Imports._


// it's very very important that this class is totally immutable!
case class ClickStream(allClicks: GenSeq[Event], lastUpdated: DateTime, firstUpdated: DateTime) {
  lazy val userClicks = allClicks filterNot isBot

  def +(e: Event) = copy(allClicks = e +: allClicks, lastUpdated = e.dt)

  def removeEventsBefore(dt: DateTime) = copy(
    allClicks = allClicks.filterNot(_.dt < dt),
    firstUpdated = if (firstUpdated > dt) firstUpdated else dt
  )

  def ageMs = DateTime.now.millis - lastUpdated.millis

  private def isBot(e: Event) =
    e.userAgent.startsWith("facebookexternalhit") ||
    e.userAgent == "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.10) Gecko/2009042316 Firefox/3.0.10 (.NET CLR 3.5.30729)"

  lazy val timePeriodMillis = lastUpdated.millis - firstUpdated.millis
  lazy val secs = timePeriodMillis / 1000
}


class ClickStreamActor extends Actor with ActorLogging {
  var clickStream = ClickStream(Nil.par, DateTime.now, DateTime.now)

  import ClickStreamActor._

  protected def receive = {
    case e: Event => {
      clickStream += e
    }

    case TruncateClickStream => {
      log.info("Truncating click stream (size=%d)" format clickStream.allClicks.size)
      clickStream = clickStream.removeEventsBefore(DateTime.now - 15.minutes)
      log.info("Truncated click stream (size=%d)" format clickStream.allClicks.size)
    }

    case GetClickStream => sender ! clickStream

    case SendClickStreamTo(actor) => actor ! clickStream

  }
}

object ClickStreamActor {
  sealed trait Messages
  case object TruncateClickStream extends Messages
  case object GetClickStream extends Messages
  case class SendClickStreamTo(actor: ActorRef) extends Messages

}









