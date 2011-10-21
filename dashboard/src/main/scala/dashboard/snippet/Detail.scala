package dashboard
package snippet

import comet.TopTenServer

import net.liftweb.util.Helpers._
import lib.HitReport

class Detail {
  val topTen = TopTenServer.topTen

  def render = {
    "*" #> topTen.hits.zipWithIndex.map { case (hit: HitReport, idx: Int) =>
      "* [name]" #> (idx+1).toString &
      ".toplink [href]" #> ("http://www.guardian.co.uk" + hit.url) &
      ".toplink *" #> hit.url &
      ".total-hits" #> "%d hits (%.1f%%)".format(hit.hits, hit.percent) &
      "li" #> hit.referrers.sorted.map { url =>
        "li *" #> url
      }

    }

  }

}