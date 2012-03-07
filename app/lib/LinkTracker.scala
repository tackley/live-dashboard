package lib

import akka.actor.ActorSystem
import akka.event.Logging._
import akka.agent.Agent._
import akka.agent.Agent
import akka.event.Logging
import play.api.libs.ws.WS
import play.api.http.HeaderNames
import org.jsoup.Jsoup
import collection.JavaConversions._
import java.net.URL

class LinkTracker(url: String)(implicit actorSys: ActorSystem)  {
  private val log = Logging(actorSys, this.getClass)
  
  // the current outgoing links on this front
  val links = Agent[List[String]](Nil)
  
  def refresh() {
    import HeaderNames._
    
    links sendOff { l =>
      val retrievedLinksPromise = WS.url(url)
        .withHeaders(USER_AGENT -> "SEO live dashboard; contact graham.tackley@guardian.co.uk")
        .get()
        .map { r =>
          val doc = Jsoup.parse(r.body, url)

          doc.select("a[href^=http:]")
            .map(_.attr("href"))
            .map(new URL(_))
            .filter(_.getHost.endsWith("guardian.co.uk"))
            .map(_.getPath.dropWhile(_ == '/'))
            .toList
        }

      val retrievedLinks = retrievedLinksPromise.await.get

      log.info("%d links on %s" format (retrievedLinks.size, url))

      retrievedLinks
    }
  }

}
