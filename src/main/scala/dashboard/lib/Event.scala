package dashboard.lib

import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime


case class Event(
  ip: String,
  dt: DateTime,
  url: String,
  method: String,
  responseCode: Int,
  referrer: Option[String],
  userAgent: String,
  geo: String) {

  lazy val path = url takeWhile ('?' !=)

  lazy val asLogString = """%15s [%s] "%s %s" %d "%s" "%s" "%s"""".format(
    ip, Event.dateFormat.print(dt), method, url, responseCode, referrer.getOrElse("-"), userAgent, geo
  )
}


object Event {
  val LogRegex = """(\S*) \S* \S* \[(.*)\] "(\S*) (\S*) \S*" (\d*) \S* "(\S*)" "(.*?)" "(.*?)".*""".r
  val dateFormat = DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss Z")

  def cleanReferrer(s: String) = Option(s) filter { "-" != }

  def fromApacheLog(s: String) = s.trim() match {
    case LogRegex(ip, dt, method, url, responseCode, referrer, userAgent, geo) =>
      Some(Event(ip, dateFormat.parseDateTime(dt), url, method, responseCode.toInt, cleanReferrer(referrer), userAgent, geo))
    case _ => None
  }
}

