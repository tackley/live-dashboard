package dashboard.snippet

import net.liftweb._
import util.Helpers._
import dashboard.lib.{Backend, Calculator}

class TopTen {

  /*
/world/middle-east-live/2011/oct/20/syria-libya-middle-east-unrest-live 11.7% (1305 hits)
/world/video/2011/oct/20/unverified-footage-gaddafi-body-video/json 7.5% (844 hits)
/ 7.5% (843 hits)
/sport/2011/oct/20/india-england-live 5.2% (584 hits)
/world/middle-east-live/2011/oct/20/gaddafi-killed-sirte-falls-live 4.1% (455 hits)
/global/adcode/generate 3.6% (406 hits)
/world/gallery/2011/feb/22/muammar-gaddafi-life-in-pictures 1.9% (210 hits)
/world 1.8% (205 hits)
/business/blog/2011/oct/20/eu-crisis-emergency-talks 1.0% (113 hits)
/external/pointrollads/PointRollAds.htm 1.0% (112 hits)
/world/video/2011/oct/20/unverified-footage-gaddafi-body-video 1.0% (112 hits)
/football 0.8% (94 hits)
/discussion/handlers/recommendComment 0.8% (92 hits)

   */


  val dummyData = List(
    Calculator.HitReport("/world/middle-east-live/2011/oct/20/syria-libya-middle-east-unrest-live", 11.7, 1305),
    Calculator.HitReport("/world/video/2011/oct/20/unverified-footage-gaddafi-body-video", 7.5, 844),
    Calculator.HitReport("/", 7.5, 843),
    Calculator.HitReport("/sport/2011/oct/20/india-england-live",5.2, 584)
  )

  def render = "*" #> Backend.currentTopTen.map { hit =>
    ".toplink *" #> <a href={ "http://www.guardian.co.uk" + hit.url }>{hit.url}</a> &
    ".percent *" #> "%.1f%%".format(hit.percent)
  }


}