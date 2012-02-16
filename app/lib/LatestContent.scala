package lib

import akka.agent._
import com.gu.openplatform.contentapi.model._
import org.joda.time.DateTime
import com.gu.openplatform.contentapi.Api
import akka.actor.ActorSystem
import akka.event.Logging

class LatestContent(implicit sys: ActorSystem) {
  Api.apiKey = Some("d7bd4fkrbgkmaehrfjsbcetu")

  private val log = Logging(sys, this.getClass)
  val latest = Agent[List[Content]](Nil)
  
  def refresh() {
    // "sendOff" means this may be a slow operation, so
    // perform it not in one of the normal actor processing threads
    latest sendOff { content =>
      val lastDateTime = content.headOption.map(_.webPublicationDate) getOrElse (new DateTime().minusHours(24))

      log.info("Getting latest content published since "+ lastDateTime + "...")

      val apiNewContent: List[Content] =
        Api.search.fromDate(lastDateTime).showTags("all").orderBy("oldest").pageSize(50).results.reverse

      // because of the way we handle dates we will always get at least one item of content repeated
      // so remove stuff we've already got from the api list
      val newContent = apiNewContent.filterNot(c => content.exists(_.id == c.id))

      val result = newContent ::: content.filter(_.webPublicationDate.plusHours(24).isAfterNow)

      log.info("Content list is now " + result.size + " entries")

      result
    }
  }
}
