package lib

import xml.NodeSeq
import java.net.URL
import org.scala_tools.time.Imports._

sealed abstract class Movement { def img: Option[String] }
case class Unchanged() extends Movement { val img = None }
case class NewEntry() extends Movement { val img = Some("new") }
case class Up() extends Movement { val img = Some("up") }
case class Down() extends Movement { val img = Some("down") }


case class HitReport(url: String, percent: Double, hits: Int, hitsPerSec: Double, events: List[Event], movement: Movement = Unchanged()) {
  def summary = "%s %.1f%% (%d hits)" format(url, percent, hits)

  lazy val referrers = events flatMap { _.referrer }

  lazy val referrerHostCounts = referrers.flatMap(url => try { Some(new URL(url).getHost) } catch { case _ => None })
    .groupBy(identity).mapValues(_.size).toList.sortBy(_._2).reverse

  lazy val referrerPercents: List[(String, Double)] = referrerHostCounts.map { case (host, count) =>
    host -> (count * 100.0 / hits)
  }

  lazy val id = url.replace("/", "")

  lazy val fullUrl = "http://www.guardian.co.uk" + url

  lazy val cssClass = if (hitsPerSec >= 1.0) "high" else ""

}


case class ListsOfStuff(
  all: TopHits = TopHits(),
  everything: TopHits = TopHits(),
  content: TopHits = TopHits(),
  other: TopHits = TopHits(),
  lastUpdated: DateTime = DateTime.now,
  firstUpdated: DateTime = DateTime.now,
  totalHits: Long = 0,
  clickStreamSecs: Long = 0
) {
  import ListsOfStuff._

  private val fmt = "d MMM yyyy h:mm:ss a"

  lazy val ageString = "%s to %s" format (
    firstUpdated.toString(fmt),
    lastUpdated.toString(fmt)
  )

  lazy val hitsScaledToAllServers = totalHits * MqReader.SCALE_TO_FULL_SITE
  lazy val hitsPerSecond = if (clickStreamSecs == 0) "N/A" else (hitsScaledToAllServers / clickStreamSecs).toString

  println("hits = %d, timePeriodSecs = %d, hps = %s" format (totalHits, clickStreamSecs, hitsPerSecond))

  def diff(newList: List[HitReport], clicks: ClickStream) = {
    val (newContent, newOther) = newList.partition(isContent)

    copy(
      all = all.diff(newList take 10),
      everything = everything.diff(newList),
      content = content.diff(newContent take 20),
      other = other.diff(newOther take 20),
      lastUpdated = clicks.lastUpdated,
      firstUpdated = clicks.firstUpdated,
      totalHits = clicks.userClicks.size,
      clickStreamSecs = clicks.secs
    )
  }

  def isContent(h: HitReport) = contentMatch.findFirstIn(h.url).isDefined
}

object ListsOfStuff {
  val contentMatch = """/\d{4}/\w{3}/\d{2}""".r
}

case class TopHits(hits: List[HitReport] = Nil) {

  def diff(newList: List[HitReport]): TopHits = {
    TopHits(newList.zipWithIndex map { case (hit, idx) => diffedHit(hit, idx) })
  }

  def diffedHit(newHitReport: HitReport, newIdx: Int) = {
    val currentHitReportIdx = hits.indexWhere(_.url == newHitReport.url)

    val movement =
      if (currentHitReportIdx == -1) NewEntry()
      else if (newIdx < currentHitReportIdx) Up()
      else if (newIdx > currentHitReportIdx) Down()
      else Unchanged()

    newHitReport.copy(movement = movement)
  }
}