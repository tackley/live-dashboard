package dashboard.snippet

import net.liftweb.util.Helpers._
import dashboard.comet.TopHitsServer

class DataAge {
  def render = "*" #> TopHitsServer.topStuff.ageString
}