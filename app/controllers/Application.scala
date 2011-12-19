package controllers

import play.api._
import play.api.mvc._
import lib.Backend
import org.joda.time.DateTime
import com.gu.openplatform.contentapi.model.Tag

object Application extends Controller {
  
  def index = Action { Ok(views.html.index()) }

  def top10 = Action { Ok(views.html.top10(Backend.currentLists.get.all)) }
  def top10chart = Action { Ok(views.html.snippets.top10chart(Backend.currentLists.get.all)) }

  def top20 = Action { Ok(views.html.top20(Backend.currentLists.get))}
  def top20chart = Action { Ok(views.html.snippets.top20chart(Backend.currentLists.get)) }

  def details = Action { Ok(views.html.details(Backend.currentLists.get.everything)) }

  def search = Action { Ok(views.html.search()) }

  private def publishedContent = {
    val currentHits = Api.countsData
    Backend.last24hoursOfContent.get.map { c =>
      PublishedContent(
        c.webPublicationDate, c.webUrl, c.webTitle,
        currentHits.get(c.webUrl).map(_.toString).getOrElse("0"),
        c.sectionName.getOrElse(""),
        c.tags
      )
    }
  }

  def content = Action {
    Ok(views.html.content(publishedContent))
  }
  def contentChart = Action {
    Ok(views.html.snippets.contentChart(publishedContent))
  }


}


case class PublishedContent(
  publicationDate: DateTime,
  url: String,
  title: String,
  hitsPerSec: String,
  section: String,
  tags: List[Tag]
) {
  lazy val cssClass = hitsPerSec match {
    case "0" => "zero"
    case s if s.startsWith("0") => ""
    case "trace" => ""
    case _ => "high"
  }
}