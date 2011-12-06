package lib

import akka.actor.Actor
import org.jboss.netty.handler.codec.http.QueryStringDecoder
import scala.collection.JavaConversions._
import org.joda.time.DateTime


case class GetSearchTerms()

case class GuSearchTerm(
  dt: Long,
  q: String,
  otherParams: Map[String, String]
)

class SearchTermActor extends Actor {
  private var terms: List[GuSearchTerm] = Nil

  protected def receive = {
    case e: Event =>
      if (e.path == "/search") {
        val params = new QueryStringDecoder(e.url).getParameters.map{ case (k, v) => k -> v.head }.toMap

        for (q <- params.get("q")) {
          terms = (terms :+ GuSearchTerm(System.currentTimeMillis(), q, params - "q")).take(20)
        }
      }

    case GetSearchTerms() =>
      self.channel ! terms
  }
}

