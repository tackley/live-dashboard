package dashboard.snippet

import net.liftweb.util.Helpers._
import dashboard.comet.TopTenServer

class DataAge {
  def render = "*" #> TopTenServer.topTen.ageString
}