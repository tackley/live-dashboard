package dashboard
package comet

import lib._
import net.liftweb.actor.LiftActor
import net.liftweb.http._



object TopTenServer extends LiftActor with ListenerManager {
  private var _topTen: TheTopTen = TheTopTen()

  def createUpdate = _topTen

  def topTen = _topTen

  override def lowPriority = {
    case clickStream: ClickStream => {
      val newTopTen = Calculator.calcTopTenPaths(clickStream)
      _topTen = _topTen.diff(newTopTen, clickStream)
      updateListeners()
    }
  }
}

class TopTen extends CometActor with CometListener {
  private var topTen: TheTopTen = TheTopTen()

  def registerWith = TopTenServer

  override def lowPriority = {
    case t: TheTopTen => topTen = t; reRender()
  }

  def render = {
    "tr" #> (topTen.hits.zipWithIndex.map { case (hit: HitReport, idx: Int) =>
      ".toplink" #> <a href={ "details#" + (idx+1)}>{hit.url}</a> &
      ".percent *" #> "%.1f%%".format(hit.percent) &
      ".mover *" #> hit.movement.imgTag &
      "li" #> hit.referrerPercents.take(5).map { case (host, percent) => "* *" #> "%.0f%% from %s".format(percent, host) }
    }) &
    ".latest-data" #> topTen.ageString
  }

}