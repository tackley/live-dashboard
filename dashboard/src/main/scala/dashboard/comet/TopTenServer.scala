package dashboard
package comet

import lib._
import net.liftweb.actor.LiftActor
import net.liftweb.http._

case class TheTopTen(l: List[Calculator.HitReport] = Nil)

object TopTenServer extends LiftActor with ListenerManager {
  private var topTen = TheTopTen()

  def createUpdate = topTen

  override def lowPriority = {
    case t: TheTopTen => {
      topTen = t
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
    "tr" #> topTen.l.map { hit =>
      ".toplink *" #> <a href={ "http://www.guardian.co.uk" + hit.url }>{hit.url}</a> &
        ".percent *" #> "%.1f%%".format(hit.percent)
    }
  }

}