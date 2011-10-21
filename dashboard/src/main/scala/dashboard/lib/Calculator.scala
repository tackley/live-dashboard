package dashboard.lib

object Calculator {


  def calcTopTenPaths(clickStream: ClickStream) = {
    val totalClicks = clickStream.clicks.size
    val clicksPerPath = clickStream.clicks.groupBy(_.path).map {
      case (k, v) => (k, v.size)
    }.toList
    val topTen = clicksPerPath.sortBy(_._2).reverse.take(10)

    topTen map {
      case (url, hitCount) => HitReport(url, hitCount.toDouble * 100 / totalClicks, hitCount)
    }
  }


}


