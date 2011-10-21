package dashboard
package comet

import lib._
import net.liftweb.actor.LiftActor
import net.liftweb.http._



object TopTenServer extends LiftActor with ListenerManager {
  private var topTen = TheTopTen()

  def createUpdate = topTen

  override def lowPriority = {
    case clickStream: ClickStream => {
      val newTopTen = Calculator.calcTopTenPaths(clickStream)
      topTen = topTen.diff(newTopTen)
      updateListeners()
    }
  }
}

class TopTen extends CometActor with CometListener {
  private var topTen = TheTopTen()

  def registerWith = TopTenServer

  override def lowPriority = {
    case t: TheTopTen => topTen = t; reRender()
  }

  def render = {
    "tr" #> (topTen.l.map { hit: HitReport =>
      ".toplink" #> <a href={ "http://www.guardian.co.uk" + hit.url }>{hit.url}</a> &
      ".percent *" #> "%.1f%%".format(hit.percent) &
      ".mover *" #> hit.movement.imgTag &
      "li" #> hit.referrerPercents.take(5).map { case (host, percent) => "* *" #> "%.0f%% from %s".format(percent, host) }
    })
  }

}