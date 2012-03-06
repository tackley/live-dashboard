package lib

import akka.agent._
import com.gu.openplatform.contentapi.model._
import org.joda.time.DateTime
import com.gu.openplatform.contentapi.Api
import akka.actor.ActorSystem
import akka.event.Logging
import play.api.libs.ws.WS
import play.api.libs.concurrent.Promise

case class LiveDashboardContent(
  content: Content,
  isLead: Option[Boolean] = None
)

object LiveDashboardContent {
  // this is a temporary hack :)
  implicit def ourContentToApiContent(c: LiveDashboardContent): Content = c.content
}


class LatestContent(implicit sys: ActorSystem) {
  val apiKey = "d7bd4fkrbgkmaehrfjsbcetu"
  Api.apiKey = Some(apiKey)

  private val log = Logging(sys, this.getClass)
  val latest = Agent[List[LiveDashboardContent]](Nil)

  val editorialSections = "artanddesign | books | business | childrens-books-site | commentisfree | " +
    "crosswords | culture | education | environment | fashion | film | football | theguardian | " +
    "theobserver | global | global-development | law | lifeandstyle | media | money | music | news | " +
    "politics | science | society | sport | stage | technology | tv-and-radio | travel | uk | world";
  
  def refresh() {
    // "sendOff" means this may be a slow operation, so don't
    // perform it in one of the normal actor processing threads
    latest sendOff { content =>
      val lastDateTime = content.headOption.map(_.webPublicationDate) getOrElse (new DateTime().minusHours(4))

      log.info("Getting latest content published since "+ lastDateTime + "...")

      val apiNewContent: List[Content] =
        Api.search.fromDate(lastDateTime).showTags("all")
          .orderBy("oldest").showFields("trailText")
          .showMedia("picture")
          .section(editorialSections)
          .pageSize(50).results
          .reverse

      // because of the way we handle dates we will always get at least one item of content repeated
      // so remove stuff we've already got from the api list
      val newContent = apiNewContent.filterNot(c => content.exists(_.id == c.id)).map(LiveDashboardContent(_))
      
      log.info("New content size is " + newContent.size)
      if (!newContent.isEmpty) latest.sendOff(findLeadContent _)

      val result = newContent ::: content.filter(_.webPublicationDate.plusHours(4).isAfterNow)

      log.info("Content list is now " + result.size + " entries")

      result
    }
  }
  
  private def findLeadContent(contentList: List[LiveDashboardContent]): List[LiveDashboardContent] = {
    def leadContentForTag(tagId: String): Promise[Seq[String]] = {
      WS.url("http://content.guardianapis.com/%s.json" format tagId)
        .withQueryString("api-key" -> apiKey)
        .get()
        .map { r => if (r.body.startsWith("<")) { log.info("*** tagId = " + tagId + " *** " + r.body) }; r }
        .map { r => (r.json \ "response" \ "leadContent" \\ "id").map(_.as[String]) }
    }
    
    val contentMissingLeadStatus = contentList.filter(_.isLead.isEmpty)
    val leadSections = contentMissingLeadStatus.flatMap(_.sectionId).sorted.distinct

    log.info("Need to find lead content status for " + leadSections)
    
    val leadItemsPromise = for {
      section <- leadSections
    } yield {
      val sectionTag = section + "/" + section
      section -> leadContentForTag(sectionTag)
    }

    log.info("leadItemsPromise = " + leadItemsPromise)
    
    // now redeem those promises
    val leadItems = leadItemsPromise.flatMap { 
      case (section, promise) =>
        promise.await.fold(
          onError = { t =>
            log.error(t, "error getting info for section %s" format section)
            Nil
          },
          onSuccess = { r => List(section -> r) }
        )
    }.toMap

    log.info("leadItems = " + leadItems)
    
    val result = contentList.map {
      case c if c.isLead.isDefined => c
      case c if c.sectionId.isEmpty => c.copy(isLead = Some(false))
      case c =>
        val section = c.sectionId.get
        val leadList = leadItems.get(section).getOrElse(Nil)
      
        val isLead = leadList contains c.id
      
        log.info("%s (%s) -> isLead = %s" format (c.id, section, isLead))
        log.info("available lead content for this section: %s" format leadList.mkString("\t\n"))
        c.copy(isLead = Some(isLead))
      
    }
    
    
    result
  }
}
