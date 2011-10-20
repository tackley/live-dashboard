package dashboard.lib

import akka.actor._
import akka.actor.Actor._
import akka.event.EventHandler
import collection.GenSeq
import org.scala_tools.time.Imports._
import java.util.concurrent.TimeUnit


object Calculator {
  case class HitReport(url: String, percent: Double, hits: Int) {
    def summary = "%s %.1f%% (%d hits)" format (url, percent, hits)
  }

  def calcTopTenPaths(clickStream: ClickStream) = {
    val totalClicks = clickStream.clicks.size
    val clicksPerPath = clickStream.clicks.groupBy(_.path).map { case (k, v) => (k, v.size) }.toList
    val topTen = clicksPerPath.sortBy(_._2).reverse.take(10)

    topTen map { case (url, hitCount) => HitReport(url, hitCount.toDouble * 100 / totalClicks, hitCount )}
  }


}


// it's very very important that this class is totally immutable!







