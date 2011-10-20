package dashboard.lib

import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTime


case class Event(
  dt: DateTime,
  url: String,
  method: String,
  responseCode: Int,
  referrer: Option[String]) {

  lazy val path = url takeWhile ('?' !=)
}


object Event {
  val LogRegex = """\S* \S* \S* \[(.*)\] "(\S*) (\S*) \S*" (\d*) \S* "(\S*)".*""".r
  val dateFormat = DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss Z")

  def cleanReferrer(s: String) = Option(s) filter { "-" != }

  def fromApacheLog(s: String) = s.trim() match {
    case LogRegex(dt, method, url, responseCode, referrer) =>
      Some(Event(dateFormat.parseDateTime(dt), url, method, responseCode.toInt, cleanReferrer(referrer)))
    case _ => None
  }
}

