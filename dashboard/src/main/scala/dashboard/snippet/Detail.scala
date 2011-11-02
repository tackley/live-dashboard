package dashboard
package snippet

import comet.TopHitsServer

import net.liftweb.util.Helpers._
import lib.{Event, HitReport}

class Detail {
  val topTen = TopHitsServer.topStuff.everything


  def render = {
    "*" #> topTen.hits.zipWithIndex.map { case (hit: HitReport, idx: Int) =>
      "* [id]" #> hit.url.replace("/", "") &
      ".toplink [href]" #> ("http://www.guardian.co.uk" + hit.url) &
      ".toplink *" #> hit.url &
      ".total-hits" #> "%d hits (%.1f%%)".format(hit.hits, hit.percent) &
      "li" #> groupAndSortReferrers(hit.referrers).map { info => "li *" #> info } &
      "pre *" #> formatEvents(hit.events.sortBy(_.dt.getMillis))
    }
  }

  def formatEvents(list: List[Event]) = list map { _.asLogString } mkString("\n")

  def groupAndSortReferrers(refs: List[String]) = refs.groupBy(identity).toList.sortBy(_._1).map {
    case (referrer, list) if (list.size == 1) => <a href={referrer}>{referrer}</a>
    case (referrer, list) => <xml:group><a href={referrer}>{referrer}</a> ({list.size})</xml:group>
  }

}