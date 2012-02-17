package controllers

import play.api._
import play.api.mvc._
import net.liftweb.json._
import lib.Backend
import org.joda.time.DateTime

object Api extends Controller {
  implicit val formats = Serialization.formats(NoTypeHints) ++ ext.JodaTimeSerializers.all


  def withCallback(callback: Option[String])(block: => String) = {
    Ok(callback map { _ + "(" + block + ")" } getOrElse block).as("application/javascript")
  }

  def countsData = Backend.currentHits
          .map{ hit => hit.fullUrl -> tidy("%.1f".format(hit.hitsPerSec)) }.toMap

  def counts(callback: Option[String]) = Action {
    withCallback(callback) {
      Serialization.write(countsData)
    }
  }

  def search(callback: Option[String], since: Long) = Action {
    withCallback(callback) {
      val response = Backend.liveSearchTerms.filter(_.dt > since).sortBy(_.dt)
      Serialization.write(response)
    }
  }


  case class ApiContent(path: String, title: String, publishDate: Long, publishDateString: String)

  def content(callback: Option[String], since: Long) = Action {
    withCallback(callback) {
      val content = Backend.publishedContent.map { c =>
        ApiContent(
          path = "/" + c.id,
          title = c.webTitle,
          publishDate = c.webPublicationDate.getMillis,
          publishDateString = c.webPublicationDate.toString
        )
      }.filter(_.publishDate > since)

      Serialization.write(content)
    }
  }

  private def tidy(s: String) = s match {
    case "0.0" => "trace"
    case other => other
  }

}
