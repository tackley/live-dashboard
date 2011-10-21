package dashboard.lib

import xml.NodeSeq

sealed abstract class Movement { def imgTag: NodeSeq }
case class Unchanged() extends Movement { val imgTag = NodeSeq.Empty }
case class Up() extends Movement { val imgTag = <img src="up_arrow_icon.png" alt="Up"/> }
case class Down() extends Movement { val imgTag = <img src="down_arrow_icon.png" alt="Down"/> }


case class HitReport(url: String, percent: Double, hits: Int, movement: Movement = Unchanged()) {
  def summary = "%s %.1f%% (%d hits)" format(url, percent, hits)
}



case class TheTopTen(l: List[HitReport] = Nil) {
  def diff(newList: List[HitReport]): TheTopTen = {
    TheTopTen(newList map diffedHit)
  }

  def diffedHit(newHitReport: HitReport) = {
    val currentHitReport = l.find(_.url == newHitReport.url)

    val movement = currentHitReport.map { c =>
      if (newHitReport.percent > c.percent) Up() else Down()
    } getOrElse Unchanged()

    newHitReport.copy(movement = movement)
  }
}