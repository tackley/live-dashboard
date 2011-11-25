package dashboard.lib

object Calculator {


  def calcTopPaths(clickStream: ClickStream) = {
    val totalClicks = clickStream.userClicks.size
    val clicksPerPath = clickStream.userClicks.groupBy(_.path).map {
      case (k, v) => (k, v, v.size)
    }.toList
    val topTen = clicksPerPath.sortBy(_._3).reverse

    topTen map {
      case (url, hits, hitCount) =>
        HitReport(
          url = url,
          percent = hitCount.toDouble * 100 / totalClicks,
          hits = hitCount,
          hitsPerSec = (hitCount.toDouble / clickStream.secs) * MqReader.SCALE_TO_FULL_SITE,
          events = hits.toList)
    }
  }


}


