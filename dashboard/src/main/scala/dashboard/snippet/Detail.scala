package dashboard
package snippet

import comet.TopHitsServer

import net.liftweb.util.Helpers._
import lib.HitReport

class Detail {
  val topTen = TopHitsServer.topStuff.all

  def render = {
    "*" #> topTen.hits.zipWithIndex.map { case (hit: HitReport, idx: Int) =>
      "* [id]" #> hit.url.replace("/", "") &
      ".toplink [href]" #> ("http://www.guardian.co.uk" + hit.url) &
      ".toplink *" #> hit.url &
      ".total-hits" #> "%d hits (%.1f%%)".format(hit.hits, hit.percent) &
      "li" #> hit.referrers.sorted.map { url =>
        "li *" #> url
      }

    }

  }

}