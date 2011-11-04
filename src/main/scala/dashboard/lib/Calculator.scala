package dashboard.lib

object Calculator {


  def calcTopPaths(clickStream: ClickStream) = {
    val totalClicks = clickStream.userClicks.size
    val clicksPerPath = clickStream.userClicks.groupBy(_.path).map {
      case (k, v) => (k, v, v.size)
    }.toList
    val topTen = clicksPerPath.sortBy(_._3).reverse.take(50)

    topTen map {
      case (url, hits, hitCount) =>
        HitReport(url, hitCount.toDouble * 100 / totalClicks, hitCount, hits.toList)
    }
  }


}


