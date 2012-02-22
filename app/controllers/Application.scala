package controllers

import play.api._
import play.api.mvc._
import org.joda.time.DateTime
import com.gu.openplatform.contentapi.model.Tag
import lib.{HitReport, Backend}

object Application extends Controller {
  
  def index = Action { Ok(views.html.index()) }

  def top10 = Action { Ok(views.html.top10(Backend.currentLists.all)) }
  def top10chart = Action { Ok(views.html.snippets.top10chart(Backend.currentLists.all)) }

  def top20 = Action { Ok(views.html.top20(Backend.currentLists))}
  def top20chart = Action { Ok(views.html.snippets.top20chart(Backend.currentLists)) }

  def details = Action { Ok(views.html.details(Backend.currentLists.everything)) }

  def search = Action { Ok(views.html.search()) }

  private def publishedContent = {
    val currentHits = Api.fullData
    Backend.publishedContent.map { c =>
      PublishedContent(
        c.webPublicationDate, c.webUrl, c.webTitle,
        currentHits.get(c.webUrl).map(_.tidyHitsPerSec).getOrElse("0"),
        c.sectionName.getOrElse(""),
        c.safeFields.get("trailText"),
        c.tags,
        currentHits.get(c.webUrl)
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
  trailText: Option[String], 
  tags: List[Tag],
  hitReport: Option[HitReport]
) {
  lazy val cpsCssClass = hitsPerSec match {
    case "0" => "zero"
    case s if s.startsWith("0") => ""
    case "<0.1" => ""
    case "trace" => ""
    case _ => "high"
  }
  
  lazy val rowCssClass = if (hasNetworkFrontReferrer) "front-referral" else ""

  lazy val networkFrontTooltip =
    if (hasNetworkFrontReferrer) "Have seen referrals from the UK network front"
    else "No clicks to this page from the UK network front have been seen"

  lazy val networkFrontText = if (hasNetworkFrontReferrer) "NF" else ""
  
  lazy val hasNetworkFrontReferrer =
    hitReport map { _.referrers contains "http://www.guardian.co.uk/" } getOrElse false
}