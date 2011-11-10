package dashboard
package comet

import lib._
import net.liftweb.actor.LiftActor
import net.liftweb.http._



object TopHitsServer extends LiftActor with ListenerManager {
  private var _topStuff: ListsOfStuff = ListsOfStuff()

  def createUpdate = _topStuff

  def topStuff = _topStuff

  override def lowPriority = {
    case clickStream: ClickStream => {
      val newPaths = Calculator.calcTopPaths(clickStream)
      _topStuff = _topStuff.diff(newPaths, clickStream)
      updateListeners()
    }
  }
}



abstract class MovingListBase extends CometActor with CometListener {
  var topStuff: ListsOfStuff = ListsOfStuff()

  def registerWith = TopHitsServer

  override def lowPriority = {
    case t: ListsOfStuff => topStuff = t; reRender()
  }

  def renderTopHits(t: TopHits) =
    "tr" #> (t.hits.zipWithIndex.map { case (hit: HitReport, idx: Int) =>
      ".toplink" #> <a href={ "details#" + (hit.url.replace("/", ""))}>{hit.url}</a> &
      ".percent *" #> "%.1f%%".format(hit.percent) &
      ".mover *" #> hit.movement.imgTag &
      "li" #> hit.referrerPercents.take(5).map { case (host, percent) =>
        "* *" #> "%.0f%% from %s".format(percent, host) &
        "* [class]" #> referrerClass(host)
      }
    })

  private def referrerClass(host: String) =
    if (host.endsWith("guardian.co.uk") || host.endsWith("guardiannews.com")) "internal" else "external"
}

class TopTen extends MovingListBase {

  def render = {
    renderTopHits(topStuff.all) &
    ".latest-data" #> topStuff.ageString
  }

}

class TopTwentySplit extends MovingListBase {

  def render = {
    "#content-pages" #> renderTopHits(topStuff.content) &
    "#other-pages" #> renderTopHits(topStuff.other) &
    ".latest-data" #> topStuff.ageString &
    ".cpm-count *" #> topStuff.hitsPerMinute
  }

}