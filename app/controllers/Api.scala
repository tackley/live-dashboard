package controllers

import play.api._
import play.api.mvc._
import net.liftweb.json._
import lib.Backend

object Api extends Controller {
  implicit val formats = Serialization.formats(NoTypeHints)


  def withCallback(callback: Option[String])(block: => String) = {
    Ok(callback map { _ + "(" + block + ")" } getOrElse block).as("application/javascript")
  }

  def counts(callback: Option[String]) = Action {
    withCallback(callback) {
      val response = Backend.currentHits
        .map{ hit => hit.fullUrl -> tidy("%.1f".format(hit.hitsPerSec)) }.toMap

      Serialization.write(response)
    }
  }

  def search(callback: Option[String], since: Long) = Action {
    withCallback(callback) {
      val response = Backend.liveSearchTerms.get.filter(_.dt > since).sortBy(_.dt)
      Serialization.write(response)
    }
  }

  private def tidy(s: String) = s match {
    case "0.0" => "trace"
    case other => other
  }

}
