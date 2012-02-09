package lib

import akka.actor.Actor
import com.gu.openplatform.contentapi.model._
import org.joda.time.DateTime
import com.gu.openplatform.contentapi.Api

object LatestContentActor {
  case class Refresh()
  case class Get()
}

class LatestContentActor extends Actor {
  // latest published content, most recently published first
  private var latestContent: List[Content] = Nil

  Api.apiKey = Some("d7bd4fkrbgkmaehrfjsbcetu")

  /*
   This logic is a bit naive: it reads through the content api based on publication datem which is
   manually edited. Perhaps it should use "last modified" to see changes to things that have
   been publish without updating the publication date?

   As it stands it will miss content that gets published with the publication date out of order.
   */
  protected def receive = {
    case LatestContentActor.Refresh() =>
      var lastDateTime = latestContent.headOption.map(_.webPublicationDate) getOrElse (new DateTime().minusHours(24))

      log("Getting latest content published since "+ lastDateTime + "...")

      val apiNewContent: List[Content] =
        Api.search.fromDate(lastDateTime).showTags("all").orderBy("oldest").pageSize(50).results.reverse

      // because of the way we handle dates we will always get at least one item of content repeated
      // so remove stuff we've already got from the api list
      val newContent = apiNewContent.filterNot(c => latestContent.exists(_.id == c.id))

      latestContent = newContent ::: latestContent.filter(_.webPublicationDate.plusHours(24).isAfterNow)

      log("Content list is now " + latestContent.size + " entries")

    case LatestContentActor.Get() =>
      sender ! latestContent

  }

  private def log(s: String) {
    println(s)
  }
}