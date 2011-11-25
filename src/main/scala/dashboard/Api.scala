package dashboard

import comet.TopHitsServer
import net.liftweb.common.Full

import net.liftweb.json._
import net.liftweb.http.{S, JsonResponse}
import net.liftweb.http.js.JE

object Api {
  implicit val formats = Serialization.formats(NoTypeHints)

  private case class ApiCount(path: String, hitsPerSec: Double)

  def counts = {
    val cb = S.param("callback").getOrElse("fuck")
    val response = TopHitsServer.topStuff.everything.hits
      .map{ hit => "http://www.guardian.co.uk" + hit.url -> "%.1f".format(hit.hitsPerSec) }.toMap

    Full(JsonResponse(JE.Call(cb, Extraction.decompose(response))))
  }
}